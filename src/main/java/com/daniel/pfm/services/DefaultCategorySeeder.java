package com.daniel.pfm.services;

import com.daniel.pfm.enums.TransactionType;
import com.daniel.pfm.models.Category;
import com.daniel.pfm.models.User;
import com.daniel.pfm.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DefaultCategorySeeder {

    private final CategoryRepository categoryRepository;

    private record DefaultCategory(String name, TransactionType type, String color, String icon) {}

    private static final List<DefaultCategory> DEFAULTS = List.of(
        new DefaultCategory("Alimentação", TransactionType.EXPENSE, "#FF6B6B", "utensils"),
        new DefaultCategory("Transporte", TransactionType.EXPENSE, "#4ECDC4", "car"),
        new DefaultCategory("Saúde", TransactionType.EXPENSE, "#45B7D1", "heart-pulse"),
        new DefaultCategory("Moradia", TransactionType.EXPENSE, "#96CEB4", "house"),
        new DefaultCategory("Lazer", TransactionType.EXPENSE, "#FFEAA7", "gamepad-2"),
        new DefaultCategory("Educação", TransactionType.EXPENSE, "#DDA0DD", "book-open"),
        new DefaultCategory("Outros", TransactionType.EXPENSE, "#B0B0B0", "ellipsis"),
        new DefaultCategory("Salário", TransactionType.INCOME,  "#55EFC4", "briefcase"),
        new DefaultCategory("Freelance", TransactionType.INCOME,  "#00B894", "laptop"),
        new DefaultCategory("Investimentos", TransactionType.INCOME,  "#FDCB6E", "trending-up")
    );

    public void seedFor(User user) {
        List<Category> categories = DEFAULTS.stream()
                .map(d -> new Category(user, d.name(), d.type(), d.color(), d.icon()))
                .toList();
        categoryRepository.saveAll(categories);
    }
}
