package dev.kokud.recipepickerapi.recipes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;


@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
class RecipeController {
    private final RecipeService recipeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Mono<RecipeProjection> createRecipe(@RequestBody @Valid RecipeProjection recipe) {
        var user = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return user.map(Principal::getName).flatMap(name -> recipeService.createRecipe(recipe, name));
    }

    @PutMapping("{id}")
    Mono<RecipeProjection> updateRecipe(@PathVariable String id, @RequestBody @Valid RecipeProjection recipe) {
        var user = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return user.map(Principal::getName).flatMap(name -> recipeService.updateRecipe(id, recipe, name));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    Mono<Void> deleteRecipe(@PathVariable String id) {
        var user = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return user.map(Principal::getName).flatMap(name -> recipeService.deleteRecipe(name, id));
    }

    @GetMapping
    Flux<RecipeProjection> getPagedRecipes(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "ALL") RecipeCategory category) {
        var user = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return user.map(Principal::getName).flatMapMany(name -> recipeService.getRecipesPaged(name, category , page, size));
    }

    @GetMapping("{id}")
    Mono<RecipeProjection> getRecipesById(@PathVariable("id") String recipeId) {
        return recipeService.getRecipeById(recipeId);
    }

    @GetMapping("ingredient/{id}")
    Flux<RecipeProjection> getPagedRecipesByIngredientId(@PathVariable("id") String ingredientId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "ALL") RecipeCategory category) {
        var user = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return user.map(Principal::getName).flatMapMany(name -> recipeService.getRecipesPagedByIngredientId(name, ingredientId, category, page, size));
    }

    @GetMapping("myRecipes")
    Flux<RecipeProjection> getMyRecipesPaged(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "ALL") RecipeCategory category) {
        var user = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return user.map(Principal::getName).flatMapMany(name -> recipeService.getMyRecipesPaged(name, category, page, size));
    }

    @GetMapping("/owned")
    Flux<RecipeProjection> getOwnedRecipesPaged(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "ALL") RecipeCategory category) {
        var user = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return user.map(Principal::getName).flatMapMany(name -> recipeService.getOwnedRecipesPaged(name, category, page, size));
    }
}
