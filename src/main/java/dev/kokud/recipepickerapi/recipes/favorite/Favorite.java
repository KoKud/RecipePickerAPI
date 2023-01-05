package dev.kokud.recipepickerapi.recipes.favorite;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "favorites")
class Favorite {
    private String id;
    private String recipeId;
    private String userId;
}
