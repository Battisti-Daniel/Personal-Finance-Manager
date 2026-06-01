package com.daniel.pfm.services;

import com.daniel.pfm.dtos.Category.CategoryPutDTO;
import com.daniel.pfm.dtos.Category.CategoryRequestDTO;
import com.daniel.pfm.dtos.Category.CategoryResponseDTO;
import com.daniel.pfm.exceptions.CategoryAlreadyExistsException;
import com.daniel.pfm.exceptions.CategoryDoesNotExistsException;
import com.daniel.pfm.models.Category;
import com.daniel.pfm.models.User;
import com.daniel.pfm.repository.CategoryRepository;
import com.daniel.pfm.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;
    private final UserRepository userRepository;

    @Transactional
    public CategoryResponseDTO create(CategoryRequestDTO entity, String email){

        User user = userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new UsernameNotFoundException("Usuario não encontrado")
                );

        if(repository.existsByNameAndTypeAndUser(entity.getName(), entity.getType(), user)){
            throw new CategoryAlreadyExistsException();
        }

        Category category = new Category(user, entity);

        Category saved = repository.save(category);
        repository.flush();

        return new CategoryResponseDTO(saved);


    }

    public List<CategoryResponseDTO> findAll(String name) {

        User user = userRepository.findByEmail(name)
                .orElseThrow(
                        () -> new UsernameNotFoundException("Usuario não encontrado")
                );

        return repository.findAllByUser(user)
                .stream()
                .map(CategoryResponseDTO::new)
                .toList();

    }


    public CategoryResponseDTO find(UUID id, String name) {

        User user = userRepository.findByEmail(name)
                .orElseThrow(
                        () -> new UsernameNotFoundException("Usuario não encontrado")
                );

        Category category = repository.findByIdAndUser(id, user)
                .orElseThrow(CategoryDoesNotExistsException::new);

        return new CategoryResponseDTO(category);

    }

    public CategoryPutDTO update(UUID id, CategoryPutDTO entity, String name){

        User user = userRepository.findByEmail(name)
                .orElseThrow(
                        () -> new UsernameNotFoundException("Usuario não encontrado")
                );

        if (repository.existsByIdAndUser(id, user)) {
            throw new CategoryDoesNotExistsException();
        }

        Category oldValue = repository.findByIdAndUser(id, user)
                .orElseThrow(
                        CategoryDoesNotExistsException::new
        );

        Category category = changeValues(entity, oldValue);

        repository.save(category);

        return new CategoryPutDTO(category);


    }
    public void delete(UUID id, String name){

        User user = userRepository.findByEmail(name)
                .orElseThrow(
                        () -> new UsernameNotFoundException("Usuario não encontrado")
                );

        if (repository.existsByIdAndUser(id, user)) {
            throw new CategoryDoesNotExistsException();
        }

        repository.deleteById(id);

    }

    protected Category changeValues(CategoryPutDTO newEntity, Category entity){

        entity.setName(newEntity.getName() == null ? entity.getName() : newEntity.getName());
        entity.setType(newEntity.getType() == null ? entity.getType() : newEntity.getType());
        entity.setColor(newEntity.getColor() == null ? entity.getColor() : newEntity.getColor());
        entity.setIcon(newEntity.getIcon() == null ? entity.getIcon() : newEntity.getIcon());

        return entity;

    }
}
