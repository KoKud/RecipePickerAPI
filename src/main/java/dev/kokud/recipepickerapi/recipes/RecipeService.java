package dev.kokud.recipepickerapi.recipes;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Optional;

@Service
class RecipeService {
    private final RecipeRepository recipeRepository;

    RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    Mono<RecipeProjection> createRecipe(RecipeProjection recipeProjection, String user) {
        var recipe = recipeProjection.toRecipe();
        recipe.setCreatorId(user);
        return recipeRepository.insert(recipe).map(RecipeProjection::new);
    }

    Flux<RecipeProjection> getRecipesPaged(String name, RecipeCategory category, int page, int size) {
        return recipeRepository.findAll(name)
                .filter(recipe -> category == RecipeCategory.ALL || Optional.ofNullable(recipe.getCategories()).orElse(new ArrayList<>()).contains(category))
                .skip((long) page * size).take(size).map(RecipeProjection::new);

    }

    Flux<RecipeProjection> getMyRecipesPaged(String name, RecipeCategory category, int page, int size) {
        return recipeRepository.findByCreatorId(name)
                .filter(recipe -> category == RecipeCategory.ALL || Optional.ofNullable(recipe.getCategories()).orElse(new ArrayList<>()).contains(category))
                .skip((long) page * size)
                .take(size)
                .map(RecipeProjection::new);
    }

    Flux<RecipeProjection> getRecipesPagedByIngredientId(String name, String ingredientId, RecipeCategory category, int page, int size) {
        return recipeRepository.findByIngredients_Id(name, ingredientId)
                .filter(recipe -> category == RecipeCategory.ALL || Optional.ofNullable(recipe.getCategories()).orElse(new ArrayList<>()).contains(category))
                .skip((long) page * size)
                .take(size)
                .map(RecipeProjection::new);
    }

    Mono<RecipeProjection> getRecipeById(String recipeId) {
        return recipeRepository.findById(recipeId)
                .map(RecipeProjection::new);
    }

    Mono<? extends Void> deleteRecipe(String name, String id) {
        return recipeRepository.findById(id).filter(recipe -> recipe.getCreatorId().equals(name)).flatMap(recipeRepository::delete);
    }
}
