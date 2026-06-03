package com.daniel.pfm.controllers;

import com.daniel.pfm.dtos.Budget.BudgetRequestDTO;
import com.daniel.pfm.dtos.Budget.BudgetResponseDTO;
import com.daniel.pfm.dtos.Budget.BudgetUpdateDTO;
import com.daniel.pfm.services.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService service;

    @PostMapping
    public ResponseEntity<BudgetResponseDTO> create(
            @Valid @RequestBody BudgetRequestDTO dto,
            Authentication authentication) {

        BudgetResponseDTO response = service.create(dto, authentication.getName());

        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponseDTO>> findAllByMonth(
            @RequestParam String month,
            Authentication authentication) {

        List<BudgetResponseDTO> response = service.findAllByMonth(authentication.getName(), month);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody BudgetUpdateDTO dto,
            Authentication authentication) {

        BudgetResponseDTO response = service.update(id, dto.getAmount(), authentication.getName());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            Authentication authentication) {

        service.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
