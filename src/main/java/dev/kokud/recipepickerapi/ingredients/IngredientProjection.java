package dev.kokud.recipepickerapi.ingredients;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
class IngredientProjection {
    @Null
    private String id;
    @NotBlank(message = "Name is required")
    private String name;
    private String description;
    private List<IngredientCategory> categories;
    private String imageUri;

    public IngredientProjection(Ingredient ingredient) {
        this.id = ingredient.getId();
        this.name = ingredient.getName().substring(0, 1).toUpperCase() + ingredient.getName().substring(1);
        this.description = ingredient.getDescription();
        this.categories = ingredient.getCategories();
        this.imageUri = ingredient.getImageUri();
    }

    public Ingredient toIngredient(){
        return new Ingredient(
                null,
                name.toLowerCase(),
                description,
                categories,
                imageUri,
                null,
                false,
                false
        );
    }
}
