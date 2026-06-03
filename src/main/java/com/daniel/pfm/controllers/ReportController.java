package com.daniel.pfm.controllers;

import com.daniel.pfm.dtos.Summary.CategorySummaryDTO;
import com.daniel.pfm.dtos.Summary.SummaryResponseDTO;
import com.daniel.pfm.enums.TransactionType;
import com.daniel.pfm.services.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService service;

    @GetMapping("/summary")
    public ResponseEntity<SummaryResponseDTO> summary(
            @RequestParam(name = "month") String month,
            Authentication authentication
    ){

        SummaryResponseDTO response = service.getMonthlySummary(authentication.getName(), month);

        return ok().body(response);

    }

    @GetMapping("/by-category")
    public ResponseEntity<List<CategorySummaryDTO>> byCategory(
            @RequestParam(name = "month") String month,
            @RequestParam(name = "type")TransactionType type,
            Authentication authentication
            ){

        List<CategorySummaryDTO> response = service.getCategorySummary(authentication.getName(),month,type);

        return ResponseEntity.ok().body(response);

    }



}
