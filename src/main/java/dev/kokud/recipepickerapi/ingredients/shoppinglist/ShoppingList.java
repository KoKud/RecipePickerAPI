package dev.kokud.recipepickerapi.ingredients.shoppinglist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "shopping-list")
class ShoppingList {
    private String id;
    private String userId;
    private String ingredientId;
}
