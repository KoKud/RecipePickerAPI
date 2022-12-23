package dev.kokud.recipepickerapi.recipes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class Ingredient {
    private String id;
    private Integer amount;
    private Integer type;
    private Boolean optional;
}
