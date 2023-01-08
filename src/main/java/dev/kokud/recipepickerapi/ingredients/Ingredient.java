package dev.kokud.recipepickerapi.ingredients;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "ingredients")
class Ingredient {
    private String id;
    private String name;
    private String description;
    private List<IngredientCategory> categories;
    private String imageUri;
    private String creatorId;
    private Boolean banned;
    private Boolean shared;
}
