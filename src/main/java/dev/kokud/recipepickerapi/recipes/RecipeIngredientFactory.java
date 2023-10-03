package dev.kokud.recipepickerapi.recipes;

import dev.kokud.recipepickerapi.ownedingredients.IngredientCategory;
import org.springframework.stereotype.Service;

@Service
class RecipeIngredientFactory {
    RecipeIngredient from(final RecipeIngredientDto source) {
        return new RecipeIngredient(
                null,
                source.getName(),
                source.getAmount(),
                IngredientCategory.valueOf(source.getType()),
                source.getOptional()
        );
    }
    RecipeIngredient from(final RecipeIngredientDto source, final String id) {
        return new RecipeIngredient(
                id,
                source.getName(),
                source.getAmount(),
                IngredientCategory.valueOf(source.getType()),
                source.getOptional()
        );
    }
}
