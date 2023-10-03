package dev.kokud.recipepickerapi.recipes;

import dev.kokud.recipepickerapi.recipes.dto.RecipeDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
class RecipeFactory {
    private final RecipeIngredientFactory recipeIngredientFactory;
    Recipe convertDto(final RecipeDto source) {
        return new Recipe(
                source.getId(),
                source.getTitle(),
                source.getDescription(),
                source.getCreatorId(),
                source.getIngredients().stream().map(recipeIngredientFactory::from).toList(),
                source.getDirections(),
                source.getCategories().stream().map(RecipeCategory::valueOf).toList(),
                source.getImageUri(),
                source.getShared(),
                null
        );
    }
}