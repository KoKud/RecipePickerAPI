package dev.kokud.recipepickerapi.ownedingredients;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "fridge")
class OwnedIngredient {
    private String id;
    private String userId;
    private String ingredientId;
    private double amount;
    private IngredientCategory type;
}
