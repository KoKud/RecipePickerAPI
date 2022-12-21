package dev.kokud.recipepickerapi.recipes;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
class RecipeService {
    private final RecipeRepository recipeRepository;

    RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    Mono<RecipeProjection> createRecipe(RecipeProjection recipeProjection, String user){
        var recipe = recipeProjection.toRecipe();
        recipe.setCreatorId(user);
        recipeRepository.insert(recipe).subscribe();
        return Mono.just(recipeProjection);
    }

    Flux<RecipeProjection> getRecipesPaged(String name, int page, int size) {
        return recipeRepository.findAll(name)
                .skip(page * size)
                .take(size)
                .map(RecipeProjection::new);
    }

    Flux<RecipeProjection> getMyRecipesPaged(String user, int page, int size){
        return recipeRepository.findByCreatorId(user)
                .skip((long) page * size)
                .take(size)
                .map(RecipeProjection::new);
    }
}
