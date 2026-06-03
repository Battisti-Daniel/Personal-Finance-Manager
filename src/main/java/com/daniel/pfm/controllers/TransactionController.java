package com.daniel.pfm.controllers;

import com.daniel.pfm.dtos.Error.ErrorResponseDTO;
import com.daniel.pfm.dtos.Transactions.TransactionPutDTO;
import com.daniel.pfm.dtos.Transactions.TransactionRequestDTO;
import com.daniel.pfm.dtos.Transactions.TransactionResponseDTO;
import com.daniel.pfm.dtos.csv.CSVImportResponseDTO;
import com.daniel.pfm.enums.TransactionType;
import com.daniel.pfm.models.IdempotencyRecord;
import com.daniel.pfm.models.User;
import com.daniel.pfm.repository.IdempotencyRepository;
import com.daniel.pfm.repository.UserRepository;
import com.daniel.pfm.services.TransactionService;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService service;
    private final IdempotencyRepository idempotencyRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @PostMapping("")
    @Transactional
    public ResponseEntity<TransactionResponseDTO> create(
            @Valid @RequestBody TransactionRequestDTO entity,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            Authentication authentication) throws JacksonException {

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

            try {
                idempotencyRepository.saveAndFlush(
                        new IdempotencyRecord(idempotencyKey, user, "", 0)
                );
            } catch (DataIntegrityViolationException e) {
                IdempotencyRecord existing = idempotencyRepository
                        .findByIdemKeyAndUser(idempotencyKey, user).orElseThrow();

                if (existing.getResponseBody().isBlank()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(null);
                }

                TransactionResponseDTO cached = objectMapper.readValue(
                        existing.getResponseBody(), TransactionResponseDTO.class);
                return ResponseEntity.status(existing.getStatusCode()).body(cached);
            }

            TransactionResponseDTO response = service.create(entity, authentication.getName());

            idempotencyRepository.updateResponse(
                    idempotencyKey, user,
                    objectMapper.writeValueAsString(response),
                    HttpStatus.CREATED.value()
            );

            URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                    .path("/{id}").buildAndExpand(response.getId()).toUri();
            return ResponseEntity.created(uri).body(response);
        }

        TransactionResponseDTO response = service.create(entity, authentication.getName());
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> detail(@PathVariable UUID id, Authentication authentication) {

        TransactionResponseDTO response = service.detail(id, authentication.getName());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("")
    public ResponseEntity<Page<TransactionResponseDTO>> page(
            Authentication authentication,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) TransactionType type,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<TransactionResponseDTO> response = service.findAll(authentication.getName(), month, categoryId, type, pageable);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody TransactionPutDTO transactionRequestDTO,
            Authentication authentication) {

        if (transactionRequestDTO.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        TransactionResponseDTO response = service.update(id, transactionRequestDTO, authentication.getName());
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, Authentication authentication) {

        service.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    public ResponseEntity<?> importCsv(@RequestParam MultipartFile file, Authentication authentication) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(400, List.of("Arquivo não pode ser vazio")));
        }

        if (!Objects.equals(file.getContentType(), "text/csv")) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(400, List.of("Arquivo deve ser CSV")));
        }

        CSVImportResponseDTO response = service.importCsv(file, authentication.getName());

        if (response.getRejected() > 0) {
            return ResponseEntity.unprocessableEntity().body(response);
        }

        return ResponseEntity.ok().body(response);
    }
}
