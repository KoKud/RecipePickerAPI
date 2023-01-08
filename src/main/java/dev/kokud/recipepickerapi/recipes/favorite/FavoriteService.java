package dev.kokud.recipepickerapi.recipes.favorite;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
class FavoriteService {

    private final FavoriteRecipeRepository favoriteRecipeRepository;

    public Mono<Boolean> updateFavoriteStatusForRecipe(String id, String name) {
        return favoriteRecipeRepository.findByRecipeIdAndUserId(id, name)
                .flatMap(favorite -> favoriteRecipeRepository.delete(favorite).thenReturn(false))
                .switchIfEmpty(favoriteRecipeRepository.insert(new Favorite(null, id, name)).thenReturn(true));
    }

    public Flux<FavoriteProjection> getFavoriteRecipes(String s) {
        return favoriteRecipeRepository.findByUserId(s).map(FavoriteProjection::new);
    }
}
