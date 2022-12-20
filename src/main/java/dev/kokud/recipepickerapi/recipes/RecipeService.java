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

    Flux<RecipeProjection> getRecipes(String name) {
        return recipeRepository.findAll(name).map(RecipeProjection::new);
    }

    Flux<RecipeProjection> getMyRecipes(String user){
        return recipeRepository.findByCreatorId(user).map(RecipeProjection::new);
    }

    Flux<RecipeProjection> getMyRecipesPaged(String user, int page, int size){
        return recipeRepository.findByCreatorId(user)
                .skip((long) page * size)
                .take(size)
                .map(RecipeProjection::new);
    }
}
