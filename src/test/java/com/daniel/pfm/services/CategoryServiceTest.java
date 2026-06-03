package com.daniel.pfm.services;

import com.daniel.pfm.dtos.Category.CategoryPutDTO;
import com.daniel.pfm.dtos.Category.CategoryRequestDTO;
import com.daniel.pfm.dtos.Category.CategoryResponseDTO;
import com.daniel.pfm.dtos.User.UserRequestDTO;
import com.daniel.pfm.enums.TransactionType;
import com.daniel.pfm.exceptions.CategoryAlreadyExistsException;
import com.daniel.pfm.exceptions.CategoryDoesNotExistsException;
import com.daniel.pfm.models.Category;
import com.daniel.pfm.models.User;
import com.daniel.pfm.repository.CategoryRepository;
import com.daniel.pfm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @InjectMocks
    private CategoryService service;

    @Mock
    private CategoryRepository repository;

    @Mock
    private UserRepository userRepository;

    private User user;
    private Category category;
    private CategoryRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        user = new User(new UserRequestDTO(
                "daniel@email.com",
                "hashedPassword",
                "Daniel",
                "device-123"
        ), "hashedPassword");

        requestDTO = new CategoryRequestDTO(
                "Alimentação",
                TransactionType.EXPENSE,
                "#FF0000",
                "food-icon"
        );

        category = new Category(user, requestDTO);
        category.setId(UUID.randomUUID());
    }

    // ==================== CREATE ====================

    @Test
    void shouldCreateCategory() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(repository.existsByNameAndTypeAndUser(requestDTO.getName(), requestDTO.getType(), user)).thenReturn(false);
        when(repository.save(any(Category.class))).thenReturn(category);

        CategoryResponseDTO response = service.create(requestDTO, user.getEmail());

        assertNotNull(response);
        assertEquals(requestDTO.getName(), response.getName());
        assertEquals(requestDTO.getType(), response.getType());
    }

    @Test
    void shouldThrowExceptionWhenCategoryAlreadyExists() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(repository.existsByNameAndTypeAndUser(requestDTO.getName(), requestDTO.getType(), user)).thenReturn(true);

        assertThrows(CategoryAlreadyExistsException.class, () ->
                service.create(requestDTO, user.getEmail())
        );

        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundOnCreate() {
        when(userRepository.findByEmail("notfound@email.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                service.create(requestDTO, "notfound@email.com")
        );
    }

    // ==================== FIND ALL ====================

    @Test
    void shouldReturnAllCategoriesForUser() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(repository.findAllByUser(user)).thenReturn(List.of(category));

        List<CategoryResponseDTO> response = service.findAll(user.getEmail());

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(category.getName(), response.get(0).getName());
    }

    // ==================== FIND ====================

    @Test
    void shouldReturnCategoryById() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(repository.findByIdAndUser(category.getId(), user)).thenReturn(Optional.of(category));

        CategoryResponseDTO response = service.find(category.getId(), user.getEmail());

        assertNotNull(response);
        assertEquals(category.getId(), response.getId());
        assertEquals(category.getName(), response.getName());
    }

    @Test
    void shouldThrowExceptionWhenCategoryNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(repository.findByIdAndUser(any(), eq(user))).thenReturn(Optional.empty());

        assertThrows(CategoryDoesNotExistsException.class, () ->
                service.find(UUID.randomUUID(), user.getEmail())
        );
    }

    @Test
    void shouldThrowExceptionWhenUserNotOwnerOfCategory() {
        User otherUser = new User(new UserRequestDTO(
                "outro@email.com", "pass", "Outro", "device-456"
        ), "pass");

        when(userRepository.findByEmail(otherUser.getEmail())).thenReturn(Optional.of(otherUser));
        when(repository.findByIdAndUser(category.getId(), otherUser)).thenReturn(Optional.empty());

        assertThrows(CategoryDoesNotExistsException.class, () ->
                service.find(category.getId(), otherUser.getEmail())
        );
    }

    // ==================== UPDATE ====================

    @Test
    void shouldUpdateCategory() {
        CategoryPutDTO putDTO = new CategoryPutDTO("Nova categoria", null, "#00FF00", null);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(repository.findByIdAndUser(category.getId(), user)).thenReturn(Optional.of(category));
        when(repository.save(any(Category.class))).thenReturn(category);

        CategoryPutDTO response = service.update(category.getId(), putDTO, user.getEmail());

        assertNotNull(response);
        assertEquals("Nova categoria", response.getName());
        assertEquals("#00FF00", response.getColor());
    }

    @Test
    void shouldThrowExceptionWhenCategoryNotFoundOnUpdate() {
        CategoryPutDTO putDTO = new CategoryPutDTO("Nome", null, null, null);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(repository.findByIdAndUser(any(), eq(user))).thenReturn(Optional.empty());

        assertThrows(CategoryDoesNotExistsException.class, () ->
                service.update(UUID.randomUUID(), putDTO, user.getEmail())
        );
    }

    // ==================== DELETE ====================

    @Test
    void shouldDeleteCategory() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(repository.existsByIdAndUser(category.getId(), user)).thenReturn(true);

        service.delete(category.getId(), user.getEmail());

        verify(repository, times(1)).deleteById(category.getId());
    }

    @Test
    void shouldThrowExceptionWhenCategoryNotFoundOnDelete() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(repository.existsByIdAndUser(any(), eq(user))).thenReturn(false);

        assertThrows(CategoryDoesNotExistsException.class, () ->
                service.delete(UUID.randomUUID(), user.getEmail())
        );
    }

    @Test
    void shouldThrowExceptionWhenUserNotOwnerOnDelete() {
        User otherUser = new User(new UserRequestDTO(
                "outro@email.com", "pass", "Outro", "device-456"
        ), "pass");

        when(userRepository.findByEmail(otherUser.getEmail())).thenReturn(Optional.of(otherUser));
        when(repository.existsByIdAndUser(category.getId(), otherUser)).thenReturn(false);

        assertThrows(CategoryDoesNotExistsException.class, () ->
                service.delete(category.getId(), otherUser.getEmail())
        );
    }
}
