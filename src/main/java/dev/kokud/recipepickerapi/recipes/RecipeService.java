package dev.kokud.recipepickerapi.recipes;

import dev.kokud.recipepickerapi.ingredients.owned.OwnedIngredientService;
import dev.kokud.recipepickerapi.recipes.favorite.FavoriteProjection;
import dev.kokud.recipepickerapi.recipes.favorite.FavoriteService;
import dev.kokud.recipepickerapi.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class RecipeService {
    private final RecipeRepository recipeRepository;
    private final FavoriteService favoriteService;
    private final OwnedIngredientService ownedIngredientService;
    private final UserService userService;

    Mono<RecipeProjection> createRecipe(RecipeProjection recipeProjection, String user) {
        var recipe = recipeProjection.toRecipe();

        return userService.getUser(user)
                .map(userData ->
                        !Optional.ofNullable(userData.getBanned()).orElse(LocalDateTime.now()).isAfter(LocalDateTime.now())
                                && userData.getAutoShare())
                .flatMap(autoShare -> {
                    recipe.setCreatorId(user);
                    recipe.setShared(autoShare);
                    return recipeRepository.insert(recipe);
                }).map(RecipeProjection::new);
    }

    Flux<RecipeProjection> getRecipesPaged(String name, RecipeCategory category, String search, int page, int size) {
        if (category == RecipeCategory.FAVOURITE) return favoriteService.getFavoriteRecipes(name)
                .map(FavoriteProjection::getRecipeId)
                .skip((long) page * size).take(size)
                .collectList().flatMapMany(recipeRepository::findByIdIn)
                .filter(recipe -> recipe.getTitle().toLowerCase().contains(search.toLowerCase()))
                .map(RecipeProjection::new);

        return recipeRepository.findAll(name)
                .filter(recipe -> category == RecipeCategory.ALL || Optional.ofNullable(recipe.getCategories()).orElse(List.of()).contains(category))
                .filter(recipe -> recipe.getTitle().toLowerCase().contains(search.toLowerCase()))
                .skip((long) page * size).take(size).map(RecipeProjection::new);

    }

    Flux<RecipeProjection> getMyRecipesPaged(String name, RecipeCategory category, String search, int page, int size) {
        if (category == RecipeCategory.FAVOURITE) return favoriteService.getFavoriteRecipes(name)
                .map(FavoriteProjection::getRecipeId).collectList()
                .flatMapMany(recipeId -> recipeRepository.findByIdInAndCreatorId(recipeId, name))
                .filter(recipe -> recipe.getTitle().toLowerCase().contains(search.toLowerCase()))
                .skip((long) page * size).take(size).map(RecipeProjection::new);

        return recipeRepository.findByCreatorId(name)
                .filter(recipe -> category == RecipeCategory.ALL || Optional.ofNullable(recipe.getCategories()).orElse(List.of()).contains(category))
                .filter(recipe -> recipe.getTitle().toLowerCase().contains(search.toLowerCase()))
                .skip((long) page * size).take(size).map(RecipeProjection::new);
    }

    Flux<RecipeProjection> getRecipesPagedByIngredientId(String name, String ingredientId, RecipeCategory category, int page, int size) {
        if (category == RecipeCategory.FAVOURITE) return favoriteService.getFavoriteRecipes(name)
                .map(FavoriteProjection::getRecipeId).collectList()
                .flatMapMany(recipeIds -> recipeRepository.findByIdInAndIngredientsId(recipeIds, ingredientId))
                .skip((long) page * size).take(size).map(RecipeProjection::new);

        return recipeRepository.findByIngredientsId(name, ingredientId)
                .filter(recipe -> category == RecipeCategory.ALL || Optional.ofNullable(recipe.getCategories()).orElse(List.of()).contains(category))
                .skip((long) page * size).take(size).map(RecipeProjection::new);
    }

    Mono<RecipeProjection> getRecipeById(String recipeId) {
        return recipeRepository.findById(recipeId)
                .map(RecipeProjection::new);
    }

    Mono<Void> deleteRecipe(String name, String id) {
        return recipeRepository.findById(id).filter(recipe -> recipe.getCreatorId().equals(name)).flatMap(recipeRepository::delete);
    }

    public Mono<RecipeProjection> updateRecipe(String id, RecipeProjection recipe, String name) {
        return userService.getUser(name)
                .flatMap(userData ->
                        recipeRepository.findById(id)
                                .filter(recipe1 -> recipe1.getCreatorId().equals(name))
                                .map(recipe1 -> {
                                    recipe1.setTitle(Optional.ofNullable(recipe.getTitle()).orElse(recipe1.getTitle()));
                                    recipe1.setDescription(Optional.ofNullable(recipe.getDescription()).orElse(recipe1.getDescription()));
                                    recipe1.setIngredients(Optional.ofNullable(recipe.getIngredients()).orElse(recipe1.getIngredients()));
                                    recipe1.setDirections(Optional.ofNullable(recipe.getDirections()).orElse(recipe1.getDirections()));
                                    recipe1.setCategories(Optional.ofNullable(recipe.getCategories()).orElse(recipe1.getCategories()));
                                    recipe1.setImageUri(Optional.ofNullable(recipe.getImageUri()).orElse(recipe1.getImageUri()));
                                    var isUserBaned = Optional.ofNullable(userData.getBanned()).orElse(LocalDateTime.now()).isAfter(LocalDateTime.now());
                                    if(!isUserBaned) recipe1.setShared(Optional.ofNullable(recipe.getShared()).orElse(userData.getAutoShare()));
                                    return recipe1;
                                }).flatMap(recipeRepository::save))
                .map(RecipeProjection::new);
    }

    public Flux<RecipeProjection> getOwnedRecipesPaged(String name, RecipeCategory category, int page, int size) {
        return ownedIngredientService.getOwnedIngredients(name)
                .map(ownedIngredient ->
                        Map.entry(ownedIngredient.getIngredientId(), switch (ownedIngredient.getType()) {
                            case G -> ownedIngredient.getAmount();
                            case KG -> ownedIngredient.getAmount() * 1000;
                            case TEASPOON -> ownedIngredient.getAmount() * 7;
                            case TABLESPOON -> ownedIngredient.getAmount() * 15;
                            case MUG -> ownedIngredient.getAmount() * 250;
                            case PINCH -> ownedIngredient.getAmount() * 0.5;
                        })
                ).collectMap(Map.Entry::getKey, Map.Entry::getValue)
                .publishOn(Schedulers.boundedElastic())
                .flatMapMany(ownedIngredients -> {
                    List<String> favorites = (category == RecipeCategory.FAVOURITE)
                            ? favoriteService.getFavoriteRecipes(name).map(FavoriteProjection::getRecipeId).collectList().block()
                            : null;
                    return recipeRepository.findAll(name)
                            .filter(recipe -> {
                                var recipeIngredients = recipe.getIngredients();
                                if (recipeIngredients == null) return true;
                                for (var recipeIngredient : recipeIngredients) {
                                    var ownedIngredient = ownedIngredients.get(recipeIngredient.getId());
                                    if (ownedIngredient == null) return false;
                                    if (ownedIngredient < recipeIngredient.getAmount()) return false;
                                }
                                return true;
                            })
                            .filter(recipe -> favorites == null || favorites.contains(recipe.getId()));
                })
                .filter(recipe -> category == RecipeCategory.ALL || category == RecipeCategory.FAVOURITE || Optional.ofNullable(recipe.getCategories()).orElse(List.of()).contains(category))
                .skip((long) page * size).take(size)
                .map(RecipeProjection::new);
    }
}
