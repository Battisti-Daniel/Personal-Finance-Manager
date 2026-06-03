package com.daniel.pfm.controllers;

import com.daniel.pfm.dtos.Category.CategoryPutDTO;
import com.daniel.pfm.dtos.Category.CategoryRequestDTO;
import com.daniel.pfm.dtos.Category.CategoryResponseDTO;
import com.daniel.pfm.services.CategoryService;
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
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService service;

    @PostMapping("")
    public ResponseEntity<CategoryResponseDTO> create(@Valid @RequestBody CategoryRequestDTO entity, Authentication authentication){

        CategoryResponseDTO response = service.create(entity, authentication.getName());

        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(uri).body(response);

    }

    @GetMapping("")
    public ResponseEntity<List<CategoryResponseDTO>> findAll(Authentication authentication){

        List<CategoryResponseDTO> response = service.findAll(authentication.getName());

        return ResponseEntity.ok().body(response);

    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> find(@PathVariable UUID id, Authentication authentication){

        CategoryResponseDTO response = service.find(id, authentication.getName());

        return ResponseEntity.ok().body(response);

    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryPutDTO> put(@PathVariable UUID id, @Valid @RequestBody CategoryPutDTO entity ,Authentication authentication){

        if(entity.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        CategoryPutDTO response = service.update(id, entity, authentication.getName());

        return ResponseEntity.ok().body(response);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, Authentication authentication){

        service.delete(id, authentication.getName());

        return ResponseEntity.noContent().build();

    }



}
