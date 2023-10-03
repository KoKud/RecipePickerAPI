package dev.kokud.recipepickerapi.recipes;

import dev.kokud.recipepickerapi.recipes.dto.RecipeDto;
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
    Mono<RecipeDto> createRecipe(@RequestBody @Valid RecipeDto recipe) {
        var user = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return user.map(Principal::getName).flatMap(name -> recipeService.createRecipe(recipe, name));
    }

    @PutMapping("{id}")
    Mono<RecipeDto> updateRecipe(@PathVariable String id, @RequestBody @Valid RecipeDto recipe) {
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
    Flux<RecipeDto> getPagedRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ALL") RecipeCategory category,
            @RequestParam(defaultValue = "") String search) {
        var user = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return user.map(Principal::getName).flatMapMany(name -> recipeService.getRecipesPaged(name, category, search , page, size));
    }

    @GetMapping("{id}")
    Mono<RecipeDto> getRecipesById(@PathVariable("id") String recipeId) {
        return recipeService.getRecipeById(recipeId);
    }

    @GetMapping("ingredient/{id}")
    Flux<RecipeDto> getPagedRecipesByIngredientId(@PathVariable("id") String ingredientId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "ALL") RecipeCategory category) {
        var user = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return user.map(Principal::getName).flatMapMany(name -> recipeService.getRecipesPagedByIngredientId(name, ingredientId, category, page, size));
    }

    @GetMapping("myRecipes")
    Flux<RecipeDto> getMyRecipesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ALL") RecipeCategory category,
            @RequestParam(defaultValue = "") String search) {
        var user = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return user.map(Principal::getName).flatMapMany(name -> recipeService.getMyRecipesPaged(name, category, search, page, size));
    }

    @GetMapping("/owned")
    Flux<RecipeDto> getOwnedRecipesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ALL") RecipeCategory category) {
        var user = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return user.map(Principal::getName).flatMapMany(name -> recipeService.getOwnedRecipesPaged(name, category, page, size));
    }
}
