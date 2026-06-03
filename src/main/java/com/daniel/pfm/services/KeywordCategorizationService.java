package com.daniel.pfm.services;

import com.daniel.pfm.models.Category;
import com.daniel.pfm.models.User;
import com.daniel.pfm.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KeywordCategorizationService {

    private final CategoryRepository categoryRepository;

    private static final Map<String, String> KEYWORD_RULES = new LinkedHashMap<>();

    static {
        KEYWORD_RULES.put("mercado",       "Alimentação");
        KEYWORD_RULES.put("supermercado",  "Alimentação");
        KEYWORD_RULES.put("restaurante",   "Alimentação");
        KEYWORD_RULES.put("ifood",         "Alimentação");
        KEYWORD_RULES.put("rappi",         "Alimentação");
        KEYWORD_RULES.put("padaria",       "Alimentação");
        KEYWORD_RULES.put("lanchonete",    "Alimentação");
        KEYWORD_RULES.put("uber",          "Transporte");
        KEYWORD_RULES.put("99",            "Transporte");
        KEYWORD_RULES.put("onibus",        "Transporte");
        KEYWORD_RULES.put("metrô",         "Transporte");
        KEYWORD_RULES.put("metro",         "Transporte");
        KEYWORD_RULES.put("gasolina",      "Transporte");
        KEYWORD_RULES.put("combustivel",   "Transporte");
        KEYWORD_RULES.put("estacionamento","Transporte");
        KEYWORD_RULES.put("farmacia",      "Saúde");
        KEYWORD_RULES.put("hospital",      "Saúde");
        KEYWORD_RULES.put("clinica",       "Saúde");
        KEYWORD_RULES.put("medico",        "Saúde");
        KEYWORD_RULES.put("plano de saude","Saúde");
        KEYWORD_RULES.put("aluguel",       "Moradia");
        KEYWORD_RULES.put("condominio",    "Moradia");
        KEYWORD_RULES.put("agua",          "Moradia");
        KEYWORD_RULES.put("luz",           "Moradia");
        KEYWORD_RULES.put("energia",       "Moradia");
        KEYWORD_RULES.put("internet",      "Moradia");
        KEYWORD_RULES.put("netflix",       "Lazer");
        KEYWORD_RULES.put("spotify",       "Lazer");
        KEYWORD_RULES.put("cinema",        "Lazer");
        KEYWORD_RULES.put("steam",         "Lazer");
        KEYWORD_RULES.put("curso",         "Educação");
        KEYWORD_RULES.put("faculdade",     "Educação");
        KEYWORD_RULES.put("escola",        "Educação");
        KEYWORD_RULES.put("livro",         "Educação");
        KEYWORD_RULES.put("salario",       "Salário");
        KEYWORD_RULES.put("salário",       "Salário");
        KEYWORD_RULES.put("freelance",     "Freelance");
        KEYWORD_RULES.put("dividendo",     "Investimentos");
        KEYWORD_RULES.put("rendimento",    "Investimentos");
    }

    public Optional<Category> categorize(String description, User user) {
        if (description == null || description.isBlank()) return Optional.empty();

        String lowerDesc = description.toLowerCase();

        for (Map.Entry<String, String> rule : KEYWORD_RULES.entrySet()) {
            if (lowerDesc.contains(rule.getKey())) {
                List<Category> matches = categoryRepository.findAllByUser(user).stream()
                        .filter(c -> c.getName().equalsIgnoreCase(rule.getValue()))
                        .toList();
                if (!matches.isEmpty()) {
                    return Optional.of(matches.get(0));
                }
            }
        }

        return Optional.empty();
    }
}
