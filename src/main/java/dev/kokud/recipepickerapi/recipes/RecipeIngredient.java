package dev.kokud.recipepickerapi.recipes;

import dev.kokud.recipepickerapi.ownedingredients.IngredientCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class RecipeIngredient {
    private String id;
    private String name;
    private Double amount;
    private IngredientCategory type;
    private Boolean optional;

    RecipeIngredientDto toDto(){
        return RecipeIngredientDto.builder()
                .withName(name)
                .withAmount(amount)
                .withType(type.name())
                .withOptional(optional)
                .build();
    }
}
