package dev.kokud.recipepickerapi.recipes;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecipeIngredientDto {
    public static Builder builder() {
        return new Builder();
    }
    private final String name;
    private final Double amount;
    private final String type;
    private final Boolean optional;

    public static class Builder{
        private String name;
        private Double amount;
        private String type;
        private Boolean optional;

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withAmount(Double amount){
            this.amount = amount;
            return this;
        }

        public Builder withType(String type){
            this.type = type;
            return this;
        }

        public Builder withOptional(Boolean optional){
            this.optional = optional;
            return this;
        }

        public RecipeIngredientDto build(){
            return new RecipeIngredientDto(name, amount, type, optional);
        }
    }
}
