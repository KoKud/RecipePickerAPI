package dev.kokud.recipepickerapi.recipes;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Data
@NoArgsConstructor
class RecipeProjection {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    private List<Map<String, ?>> ingredients;
    private List<String> directions;
    private List<RecipeCategory> categories;
    private String imageUri;

    public RecipeProjection(Recipe recipe) {
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
