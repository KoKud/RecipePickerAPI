package dev.kokud.recipepickerapi.recipes;

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
    private List<Ingredient> ingredients;
    private List<String> directions;
    private List<RecipeCategory> categories;
    private String imageUri;
    private Boolean shared;
    private Boolean banned;
}
