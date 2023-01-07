package dev.kokud.recipepickerapi.recipes;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class RecipeService {
    private final RecipeRepository recipeRepository;

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
        return recipeRepository.findByIngredientsId(name, ingredientId)
                .filter(recipe -> category == RecipeCategory.ALL || Optional.ofNullable(recipe.getCategories()).orElse(new ArrayList<>()).contains(category))
                .skip((long) page * size)
                .take(size)
                .map(RecipeProjection::new);
    }

    Mono<RecipeProjection> getRecipeById(String recipeId) {
        return recipeRepository.findById(recipeId)
                .map(RecipeProjection::new);
    }

    Mono<Void> deleteRecipe(String name, String id) {
        return recipeRepository.findById(id).filter(recipe -> recipe.getCreatorId().equals(name)).flatMap(recipeRepository::delete);
    }

    public Mono<RecipeProjection> updateRecipe(String id, RecipeProjection recipe, String name) {
        return recipeRepository.findById(id)
                .filter(recipe1 -> recipe1.getCreatorId().equals(name))
                .map(recipe1 -> {
                    recipe1.setTitle(recipe.getTitle());
                    recipe1.setDescription(recipe.getDescription());
                    recipe1.setIngredients(recipe.getIngredients());
                    recipe1.setDirections(recipe.getDirections());
                    recipe1.setCategories(recipe.getCategories());
                    recipe1.setImageUri(recipe.getImageUri());
                    return recipe1;
                })
                .flatMap(recipeRepository::save)
                .map(RecipeProjection::new);
    }
}
