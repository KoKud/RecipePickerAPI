package dev.kokud.recipepickerapi.ownedingredients;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OwnedIngredientProjection {
    @NotBlank(message = "Name is required")
    private String ingredientId;
    private double amount;
    private IngredientCategory type;

    OwnedIngredientProjection(OwnedIngredient ownedIngredient) {
        this.ingredientId = ownedIngredient.getIngredientId();
        this.amount = ownedIngredient.getAmount();
        this.type = ownedIngredient.getType();
    }

    OwnedIngredient toOwnedIngredient(String userId) {
        return new OwnedIngredient(null, userId, ingredientId, amount, type);
    }
}
