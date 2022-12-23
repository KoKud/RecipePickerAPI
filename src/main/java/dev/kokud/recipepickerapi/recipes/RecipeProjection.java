package dev.kokud.recipepickerapi.recipes;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
class RecipeProjection {
    private String id;
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    private List<Ingredient> ingredients;
    private List<String> directions;
    private List<RecipeCategory> categories;
    private String imageUri;

    public RecipeProjection(Recipe recipe) {
        this.id = recipe.getId();
        this.title = recipe.getTitle();
        this.description = recipe.getDescription();
        this.ingredients = recipe.getIngredients();
        this.directions = recipe.getDirections();
        this.categories = recipe.getCategories();
        this.imageUri = recipe.getImageUri();
    }

    public Recipe toRecipe(){
        return new Recipe(
                null,
                title,
                description,
                null,//user,
                ingredients,
                directions,
                categories,
                imageUri,
                false,
                false
        );
    }

}
