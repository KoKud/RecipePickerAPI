package dev.kokud.recipepickerapi.recipes.favorite;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FavoriteProjection {
    private String recipeId;
    public FavoriteProjection(Favorite favorite) {
        this.recipeId = favorite.getRecipeId();
    }
}
