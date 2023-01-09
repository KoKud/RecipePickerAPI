package dev.kokud.recipepickerapi.recipes;

import dev.kokud.recipepickerapi.ingredients.owned.IngredientCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class Ingredient {
    private String id;
    private String name;
    private Integer amount;
    private IngredientCategory type;
    private Boolean optional;
}
