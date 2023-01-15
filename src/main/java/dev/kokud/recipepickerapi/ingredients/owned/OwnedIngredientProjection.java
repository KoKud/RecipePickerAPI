package dev.kokud.recipepickerapi.ingredients.owned;

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

    public OwnedIngredientProjection(OwnedIngredient ownedIngredient) {
        this.ingredientId = ownedIngredient.getIngredientId();
        this.amount = ownedIngredient.getAmount();
        this.type = ownedIngredient.getType();
    }

    public OwnedIngredient toOwnedIngredient(String userId) {
        return new OwnedIngredient(null, userId, ingredientId, amount, type);
    }
}
