package dev.kokud.recipepickerapi.ingredients.shoppinglist;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
class ShoppingListProjection {
    private String ingredientId;
    public ShoppingListProjection(ShoppingList shoppingList) {
        this.ingredientId = shoppingList.getIngredientId();
    }
}
