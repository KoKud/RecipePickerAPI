package dev.kokud.recipepickerapi.favorite;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FavoriteProjection {
    private String recipeId;
    FavoriteProjection(Favorite favorite) {
        this.recipeId = favorite.getRecipeId();
    }
}
