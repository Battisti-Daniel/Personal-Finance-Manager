package com.daniel.pfm.services;

import com.daniel.pfm.dtos.Category.CategoryRequestDTO;
import com.daniel.pfm.dtos.Transactions.TransactionPutDTO;
import com.daniel.pfm.dtos.Transactions.TransactionRequestDTO;
import com.daniel.pfm.dtos.Transactions.TransactionResponseDTO;
import com.daniel.pfm.dtos.User.UserRequestDTO;
import com.daniel.pfm.enums.TransactionType;
import com.daniel.pfm.exceptions.TransactionalNotFoundException;
import com.daniel.pfm.models.Category;
import com.daniel.pfm.models.Transaction;
import com.daniel.pfm.models.User;
import com.daniel.pfm.repository.CategoryRepository;
import com.daniel.pfm.repository.TransactionalRepository;
import com.daniel.pfm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @InjectMocks
    private TransactionService service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionalRepository repository;

    private User user;
    private Category category;
    private TransactionRequestDTO transactionRequestDTO;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        user = new User(new UserRequestDTO(
                "daniel@email.com",
                "hashedPassword",
                "Daniel",
                "device-123"
        ), "hashedPassword");

        category = new Category(
                user,
                new CategoryRequestDTO(
                        "Obra",
                        TransactionType.EXPENSE,
                        "#FF0000",
                        "https://somelinkforsomereference.com")
        );
        category.setId(UUID.randomUUID());

        transactionRequestDTO = new TransactionRequestDTO(
                category.getId(),
                "Some Description",
                BigDecimal.valueOf(543.7),
                LocalDate.now().minusDays(3),
                "Alguma nota"
        );

        transaction = new Transaction(transactionRequestDTO, user, category);
    }

    @Test
    void shouldCreateTransaction() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(categoryRepository.findByIdAndUser(category.getId(), user)).thenReturn(Optional.of(category));
        when(repository.saveAndFlush(any(Transaction.class))).thenReturn(transaction);

        TransactionResponseDTO response = service.create(transactionRequestDTO, user.getEmail());

        assertNotNull(response);
        assertEquals(category.getId(), response.getCategoryId());
        assertEquals(transactionRequestDTO.getAmount(), response.getAmount());
        assertEquals(transactionRequestDTO.getDescription(), response.getDescription());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundOnCreate() {
        when(userRepository.findByEmail("notfound@email.com")).thenReturn(Optional.empty());

        TransactionRequestDTO dto = new TransactionRequestDTO(
                UUID.randomUUID(), "Desc", BigDecimal.ONE, LocalDate.now(), null
        );

        assertThrows(UsernameNotFoundException.class, () ->
                service.create(dto, "notfound@email.com")
        );
    }

    @Test
    void shouldThrowExceptionWhenCategoryNotFoundOnCreate() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(categoryRepository.findByIdAndUser(any(), eq(user))).thenReturn(Optional.empty());

        assertThrows(Exception.class, () ->
                service.create(transactionRequestDTO, user.getEmail())
        );
    }

    @Test
    void shouldReturnDetailFromSingleTransaction() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(repository.findByIdAndUser(transaction.getId(), user)).thenReturn(Optional.of(transaction));

        TransactionResponseDTO response = service.detail(transaction.getId(), user.getEmail());

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(category.getId(), response.getCategoryId());
        assertEquals(transactionRequestDTO.getAmount(), response.getAmount());
    }

    @Test
    void shouldThrowExceptionWhenTransactionNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(repository.findByIdAndUser(any(), eq(user))).thenReturn(Optional.empty());

        assertThrows(TransactionalNotFoundException.class, () ->
                service.detail(UUID.randomUUID(), user.getEmail())
        );
    }

    @Test
    void shouldThrowExceptionWhenUserNotOwnerOfTransaction() {
        User otherUser = new User(new UserRequestDTO(
                "outro@email.com", "pass", "Outro", "device-456"
        ), "pass");

        when(userRepository.findByEmail(otherUser.getEmail())).thenReturn(Optional.of(otherUser));
        when(repository.findByIdAndUser(transaction.getId(), otherUser)).thenReturn(Optional.empty());

        assertThrows(TransactionalNotFoundException.class, () ->
                service.detail(transaction.getId(), otherUser.getEmail())
        );
    }

    @Test
    void shouldUpdateTransaction() {
        TransactionPutDTO putDTO = mock(TransactionPutDTO.class);
        when(putDTO.getDescription()).thenReturn("Nova descricao");
        when(putDTO.getAmount()).thenReturn(BigDecimal.valueOf(999.0));
        when(putDTO.getNotes()).thenReturn(null);
        when(putDTO.getDate()).thenReturn(null);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(repository.findByIdAndUser(transaction.getId(), user)).thenReturn(Optional.of(transaction));
        when(repository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionResponseDTO response = service.update(transaction.getId(), putDTO, user.getEmail());

        assertNotNull(response);
        assertEquals("Nova descricao", response.getDescription());
        assertEquals(BigDecimal.valueOf(999.0), response.getAmount());
    }

    @Test
    void shouldThrowExceptionWhenTransactionNotFoundOnUpdate() {
        TransactionPutDTO putDTO = mock(TransactionPutDTO.class);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(repository.findByIdAndUser(any(), eq(user))).thenReturn(Optional.empty());

        assertThrows(TransactionalNotFoundException.class, () ->
                service.update(UUID.randomUUID(), putDTO, user.getEmail())
        );
    }


    @Test
    void shouldDeleteTransaction() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(repository.findByIdAndUser(transaction.getId(), user)).thenReturn(Optional.of(transaction));

        service.delete(transaction.getId(), user.getEmail());

        verify(repository, times(1)).delete(transaction);
    }

    @Test
    void shouldThrowExceptionWhenUserNotOwnerOnDelete() {
        User otherUser = new User(new UserRequestDTO(
                "outro@email.com", "pass", "Outro", "device-456"
        ), "pass");

        when(userRepository.findByEmail(otherUser.getEmail())).thenReturn(Optional.of(otherUser));
        when(repository.findByIdAndUser(transaction.getId(), otherUser)).thenReturn(Optional.empty());

        assertThrows(TransactionalNotFoundException.class, () ->
                service.delete(transaction.getId(), otherUser.getEmail())
        );
    }


    @Test
    void shouldReturnPagedTransactions() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Transaction> page = new PageImpl<>(List.of(transaction));

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<TransactionResponseDTO> response = service.findAll(user.getEmail(), null, null, null, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(category.getId(), response.getContent().get(0).getCategoryId());
    }

}
