package com.daniel.pfm.services;

import com.daniel.pfm.dtos.Transactions.TransactionRequestDTO;
import com.daniel.pfm.dtos.Transactions.TransactionResponseDTO;
import com.daniel.pfm.exceptions.CategoryDoesNotExistsException;
import com.daniel.pfm.models.Category;
import com.daniel.pfm.models.Transaction;
import com.daniel.pfm.models.User;
import com.daniel.pfm.repository.CategoryRepository;
import com.daniel.pfm.repository.TransactionalRepository;
import com.daniel.pfm.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionalRepository repository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public TransactionResponseDTO create(TransactionRequestDTO entity, String name){

        User user = userRepository.findByEmail(name)
                .orElseThrow(
                        () -> new UsernameNotFoundException("Usuario não encontrado")
                );
        Category category = categoryRepository.findByIdAndUser(entity.getCategoryId(), user)
                .orElseThrow(CategoryDoesNotExistsException::new);

        Transaction response = new Transaction(entity,user, category);

        response = repository.saveAndFlush(response);

        return new TransactionResponseDTO(response);

    }

}
