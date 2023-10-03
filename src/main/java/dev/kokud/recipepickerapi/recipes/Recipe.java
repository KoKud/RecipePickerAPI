package dev.kokud.recipepickerapi.recipes;

import dev.kokud.recipepickerapi.recipes.dto.RecipeDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "recipes")
class Recipe {
    private String id;
    private String title;
    private String description;
    private String creatorId;
    private List<RecipeIngredient> ingredients;
    private List<String> directions;
    private List<RecipeCategory> categories;
    private String imageUri;
    private Boolean shared;
    private Boolean banned;

    RecipeDto toDto() {
        return RecipeDto.builder()
                .withId(id)
                .withTitle(title)
                .withDescription(description)
                .withCreatorId(creatorId)
                .withIngredients(ingredients.stream().map(RecipeIngredient::toDto).toList())
                .withDirections(directions)
                .withCategories(categories.stream().map(RecipeCategory::name).toList())
                .withImageUri(imageUri)
                .withShared(shared)
                .build();
    }
}
