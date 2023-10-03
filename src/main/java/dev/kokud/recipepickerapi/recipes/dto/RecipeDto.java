package dev.kokud.recipepickerapi.recipes.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import dev.kokud.recipepickerapi.recipes.RecipeIngredientDto;
import jakarta.validation.constraints.Null;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonDeserialize(builder = RecipeDto.Builder.class)
public class RecipeDto {
    public static Builder builder() {
        return new Builder();
    }

    @Null
    private String id;
    private String title;
    private String description;
    private String creatorId;
    private List<RecipeIngredientDto> ingredients;
    private List<String> directions;
    private List<String> categories;
    private String imageUri;
    private Boolean shared;

    public static class Builder{
        private String id;
        private String title;
        private String description;
        private String creatorId;
        private List<RecipeIngredientDto> ingredients;
        private List<String> directions;
        private List<String> categories;
        private String imageUri;
        private Boolean shared;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withTitle(String title){
            this.title = title;
            return this;
        }

        public Builder withDescription(String description){
            this.description = description;
            return this;
        }

        public Builder withCreatorId(String creatorId){
            this.creatorId = creatorId;
            return this;
        }

        public Builder withIngredients(List<RecipeIngredientDto> ingredients){
            this.ingredients = ingredients;
            return this;
        }

        public Builder withDirections(List<String> directions){
            this.directions = directions;
            return this;
        }

        public Builder withCategories(List<String> categories){
            this.categories = categories;
            return this;
        }

        public Builder withImageUri(String imageUri){
            this.imageUri = imageUri;
            return this;
        }

        public Builder withShared(Boolean shared){
            this.shared = shared;
            return this;
        }

        public RecipeDto build(){
            return new RecipeDto(id, title, description, creatorId, ingredients, directions, categories, imageUri, shared);
        }
    }
}
