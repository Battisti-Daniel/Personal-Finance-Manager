package com.daniel.pfm.services;

import com.daniel.pfm.dtos.Error.CsvImportErrorDTO;
import com.daniel.pfm.dtos.Transactions.TransactionPutDTO;
import com.daniel.pfm.dtos.Transactions.TransactionRequestDTO;
import com.daniel.pfm.dtos.Transactions.TransactionResponseDTO;
import com.daniel.pfm.dtos.csv.CSVImportResponseDTO;
import com.daniel.pfm.enums.TransactionType;
import com.daniel.pfm.exceptions.CategoryDoesNotExistsException;
import com.daniel.pfm.exceptions.TransactionalNotFoundException;
import com.daniel.pfm.models.Category;
import com.daniel.pfm.models.Transaction;
import com.daniel.pfm.models.User;
import com.daniel.pfm.repository.CategoryRepository;
import com.daniel.pfm.repository.TransactionalRepository;
import com.daniel.pfm.repository.UserRepository;
import com.daniel.pfm.specification.TransactionSpecification;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionalRepository repository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final KeywordCategorizationService keywordCategorizationService;

    @Transactional
    public TransactionResponseDTO create(TransactionRequestDTO entity, String name) {

        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        Category category = categoryRepository.findByIdAndUser(entity.getCategoryId(), user)
                .orElseThrow(CategoryDoesNotExistsException::new);

        Transaction response = repository.saveAndFlush(new Transaction(entity, user, category));

        return new TransactionResponseDTO(response);
    }

    public TransactionResponseDTO detail(UUID id, String name) {

        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        Transaction transaction = repository.findByIdAndUserAndDeletedAtIsNull(id, user)
                .orElseThrow(TransactionalNotFoundException::new);

        return new TransactionResponseDTO(transaction);
    }

    public Page<TransactionResponseDTO> findAll(String email, String month, UUID categoryId, TransactionType type, Pageable pageable) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        Specification<Transaction> spec = Specification
                .where(TransactionSpecification.byUser(user))
                .and(TransactionSpecification.byMonth(month))
                .and(TransactionSpecification.byCategoryId(categoryId))
                .and(TransactionSpecification.byType(type))
                .and(TransactionSpecification.notDeleted());

        return repository.findAll(spec, pageable).map(TransactionResponseDTO::new);
    }

    @Transactional
    public TransactionResponseDTO update(UUID id, TransactionPutDTO transactionRequestDTO, String name) {

        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        Transaction transaction = repository.findByIdAndUserAndDeletedAtIsNull(id, user)
                .orElseThrow(TransactionalNotFoundException::new);

        transaction = changeValues(transactionRequestDTO, transaction);
        repository.save(transaction);

        return new TransactionResponseDTO(transaction);
    }

    @Transactional
    public void delete(UUID id, String name) {

        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        Transaction transaction = repository.findByIdAndUserAndDeletedAtIsNull(id, user)
                .orElseThrow(TransactionalNotFoundException::new);

        transaction.setDeletedAt(LocalDateTime.now());
        repository.save(transaction);
    }


    @Transactional
    public CSVImportResponseDTO importCsv(MultipartFile file, String name) {

        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        List<CsvImportErrorDTO> errors = new ArrayList<>();
        List<Transaction> transactions = new ArrayList<>();
        int autoCategorizedCount = 0;

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {

            reader.readNext(); // pula cabeçalho
            String[] line;
            int lineNumber = 1;

            while ((line = reader.readNext()) != null) {
                lineNumber++;
                try {
                    if (line.length < 3) {
                        errors.add(new CsvImportErrorDTO(lineNumber, "Linha com colunas insuficientes (mínimo: date, description, amount)"));
                        continue;
                    }

                    String dateStr       = line[0].trim();
                    String description   = line[1].trim();
                    String amountStr     = line[2].trim();
                    String categoryIdStr = line.length > 3 ? line[3].trim() : "";
                    String notes         = line.length > 4 ? line[4].trim() : null;

                    LocalDate date;
                    try {
                        date = LocalDate.parse(dateStr);
                    } catch (DateTimeParseException ex) {
                        errors.add(new CsvImportErrorDTO(lineNumber, "Data inválida: " + dateStr));
                        continue;
                    }

                    if (description.isBlank()) {
                        errors.add(new CsvImportErrorDTO(lineNumber, "Descrição não pode ser vazia"));
                        continue;
                    }

                    BigDecimal amount;
                    try {
                        amount = new BigDecimal(amountStr);
                        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                            errors.add(new CsvImportErrorDTO(lineNumber, "Valor deve ser maior que zero: " + amountStr));
                            continue;
                        }
                    } catch (NumberFormatException ex) {
                        errors.add(new CsvImportErrorDTO(lineNumber, "Valor inválido: " + amountStr));
                        continue;
                    }

                    // Resolução de categoria: por ID explícito ou por palavras-chave
                    Category category = null;
                    boolean autocat = false;

                    if (!categoryIdStr.isBlank()) {
                        UUID categoryId;
                        try {
                            categoryId = UUID.fromString(categoryIdStr);
                        } catch (IllegalArgumentException e) {
                            errors.add(new CsvImportErrorDTO(lineNumber, "CategoryId inválido: " + categoryIdStr));
                            continue;
                        }
                        category = categoryRepository.findByIdAndUser(categoryId, user).orElse(null);
                        if (category == null) {
                            errors.add(new CsvImportErrorDTO(lineNumber, "Categoria não está vinculada à sua conta. Por favor, verifique o ID: " + categoryIdStr));
                            continue;
                        }
                    } else {
                        Optional<Category> autoCategory = keywordCategorizationService.categorize(description, user);
                        if (autoCategory.isPresent()) {
                            category = autoCategory.get();
                            autocat = true;
                        } else {
                            errors.add(new CsvImportErrorDTO(lineNumber, "Categoria não informada e nenhuma palavra-chave reconhecida para: \"" + description + "\""));
                            continue;
                        }
                    }

                    if (autocat) autoCategorizedCount++;

                    transactions.add(new Transaction(
                            new TransactionRequestDTO(category.getId(), description, amount, date, notes),
                            user,
                            category
                    ));

                } catch (Exception e) {
                    errors.add(new CsvImportErrorDTO(lineNumber, "Erro inesperado: " + e.getMessage()));
                }
            }

            if (transactions.isEmpty() && errors.isEmpty()) {
                throw new RuntimeException("Arquivo CSV vazio ou sem transações válidas");
            }

            if (!errors.isEmpty()) {
                return new CSVImportResponseDTO(0, 0, errors.size(), errors);
            }

            repository.saveAll(transactions);
            return new CSVImportResponseDTO(transactions.size(), autoCategorizedCount, 0, List.of());

        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException("Erro ao processar o arquivo CSV: " + e.getMessage());
        }
    }

    private Transaction changeValues(TransactionPutDTO newEntity, Transaction entity) {
        if (newEntity.getDescription() != null) entity.setDescription(newEntity.getDescription());
        if (newEntity.getAmount() != null) entity.setAmount(newEntity.getAmount());
        if (newEntity.getNotes() != null) entity.setNotes(newEntity.getNotes());
        if (newEntity.getDate() != null) entity.setDate(newEntity.getDate());
        return entity;
    }
}
