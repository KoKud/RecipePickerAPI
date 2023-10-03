package dev.kokud.recipepickerapi.recipes;

import dev.kokud.recipepickerapi.favorite.FavoriteProjection;
import dev.kokud.recipepickerapi.favorite.FavoriteService;
import dev.kokud.recipepickerapi.ingredients.IngredientDto;
import dev.kokud.recipepickerapi.ingredients.IngredientService;
import dev.kokud.recipepickerapi.ownedingredients.OwnedIngredientService;
import dev.kokud.recipepickerapi.recipes.dto.RecipeDto;
import dev.kokud.recipepickerapi.users.UserFacade;
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
    private final UserFacade userFacade;
    private final RecipeFactory recipeFactory;
    private final IngredientService ingredientService;
    private final RecipeIngredientFactory recipeIngredientFactory;

    Mono<RecipeDto> createRecipe(RecipeDto recipeDto, String user) {
        return userFacade.getUser(user)
                .map(userData ->
                        !Optional.ofNullable(userData.getBanned()).orElse(LocalDateTime.now()).isAfter(LocalDateTime.now())
                                && userData.getAutoShare())
                .map(autoShare -> {
                    var recipe = recipeFactory.convertDto(recipeDto);
                    recipe.setCreatorId(user);
                    recipe.setShared(autoShare);
                    return recipe;})
                .publishOn(Schedulers.boundedElastic())
                .map(recipe -> {
                    recipe.getIngredients().forEach(ingredient -> {
                        var ingredientId = ingredientService.createIngredient(new IngredientDto(ingredient.getName()), user).block();
                        ingredient.setId(ingredientId);
                    });
                    return recipe;})
                .flatMap(recipeRepository::save).map(Recipe::toDto);
    }

    Flux<RecipeDto> getRecipesPaged(String name, RecipeCategory category, String search, int page,
                                    int size) {
        if (category == RecipeCategory.FAVOURITE) return favoriteService.getFavoriteRecipes(name)
                .map(FavoriteProjection::getRecipeId)
                .skip((long) page * size).take(size)
                .collectList().flatMapMany(recipeRepository::findByIdIn)
                .filter(recipe -> recipe.getTitle().toLowerCase().contains(search.toLowerCase()))
                .map(Recipe::toDto);

        return recipeRepository.findAll(name)
                .filter(recipe -> category == RecipeCategory.ALL || Optional.ofNullable(recipe.getCategories()).orElse(List.of()).contains(category))
                .filter(recipe -> recipe.getTitle().toLowerCase().contains(search.toLowerCase()))
                .skip((long) page * size).take(size).map(Recipe::toDto);

    }

    Flux<RecipeDto> getMyRecipesPaged(String name, RecipeCategory category, String search, int page, int size) {
        if (category == RecipeCategory.FAVOURITE) return favoriteService.getFavoriteRecipes(name)
                .map(FavoriteProjection::getRecipeId).collectList()
                .flatMapMany(recipeId -> recipeRepository.findByIdInAndCreatorId(recipeId, name))
                .filter(recipe -> recipe.getTitle().toLowerCase().contains(search.toLowerCase()))
                .skip((long) page * size).take(size).map(Recipe::toDto);

        return recipeRepository.findByCreatorId(name)
                .filter(recipe -> category == RecipeCategory.ALL || Optional.ofNullable(recipe.getCategories()).orElse(List.of()).contains(category))
                .filter(recipe -> recipe.getTitle().toLowerCase().contains(search.toLowerCase()))
                .skip((long) page * size).take(size).map(Recipe::toDto);
    }

    Flux<RecipeDto> getRecipesPagedByIngredientId(String name, String ingredientId, RecipeCategory category,
                                                  int page, int size) {
        if (category == RecipeCategory.FAVOURITE) return favoriteService.getFavoriteRecipes(name)
                .map(FavoriteProjection::getRecipeId).collectList()
                .flatMapMany(recipeIds -> recipeRepository.findByIdInAndIngredientsId(recipeIds, ingredientId))
                .skip((long) page * size).take(size).map(Recipe::toDto);

        return recipeRepository.findByIngredientsId(name, ingredientId)
                .filter(recipe -> category == RecipeCategory.ALL || Optional.ofNullable(recipe.getCategories()).orElse(List.of()).contains(category))
                .skip((long) page * size).take(size).map(Recipe::toDto);
    }

    Mono<RecipeDto> getRecipeById(String recipeId) {
        return recipeRepository.findById(recipeId)
                .map(Recipe::toDto);
    }

    Mono<Void> deleteRecipe(String name, String id) {
        return recipeRepository.findById(id).filter(recipe -> recipe.getCreatorId().equals(name)).flatMap(recipeRepository::delete);
    }

    public Mono<RecipeDto> updateRecipe(String id, RecipeDto recipe, String name) {
        return userFacade.getUser(name)
                .flatMap(userData ->
                        recipeRepository.findById(id)
                                .filter(recipe1 -> recipe1.getCreatorId().equals(name))
                                .map(recipe1 -> {
                                    recipe1.setTitle(Optional.ofNullable(recipe.getTitle()).orElse(recipe1.getTitle()));
                                    recipe1.setDescription(Optional.ofNullable(recipe.getDescription()).orElse(recipe1.getDescription()));

                                    var newIngredients = recipe.getIngredients().stream().map(ingredient -> {
                                        var ingredientId = ingredientService.createIngredient(new IngredientDto(ingredient.getName()), name).block();
                                        return recipeIngredientFactory.from(ingredient, ingredientId);
                                    }).toList();

                                    recipe1.setIngredients(newIngredients.isEmpty() ? recipe1.getIngredients() : newIngredients);
                                    recipe1.setDirections(Optional.ofNullable(recipe.getDirections()).orElse(recipe1.getDirections()));

                                    var newCategories = recipe.getCategories().stream().map(RecipeCategory::valueOf).toList();

                                    recipe1.setCategories(newCategories.isEmpty() ? recipe1.getCategories() : newCategories);
                                    recipe1.setImageUri(Optional.ofNullable(recipe.getImageUri()).orElse(recipe1.getImageUri()));
                                    var isUserBaned = Optional.ofNullable(userData.getBanned()).orElse(LocalDateTime.now()).isAfter(LocalDateTime.now());
                                    if (!isUserBaned)
                                        recipe1.setShared(Optional.ofNullable(recipe.getShared()).orElse(userData.getAutoShare()));
                                    return recipe1;
                                }).flatMap(recipeRepository::save))
                .map(Recipe::toDto);
    }

    public Flux<RecipeDto> getOwnedRecipesPaged(String name, RecipeCategory category, int page, int size) {
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
                                    if (Boolean.TRUE.equals(recipeIngredient.getOptional())) continue;
                                    if (ownedIngredient == null) return false;
                                    if (ownedIngredient < recipeIngredient.getAmount()) return false;
                                }
                                return true;
                            })
                            .filter(recipe -> favorites == null || favorites.contains(recipe.getId()));
                })
                .filter(recipe -> category == RecipeCategory.ALL || category == RecipeCategory.FAVOURITE || Optional.ofNullable(recipe.getCategories()).orElse(List.of()).contains(category))
                .skip((long) page * size).take(size)
                .map(Recipe::toDto);
    }
}
