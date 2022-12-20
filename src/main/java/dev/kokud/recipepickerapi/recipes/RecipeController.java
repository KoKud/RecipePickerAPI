package dev.kokud.recipepickerapi.recipes;

import jakarta.validation.Valid;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;

@RestController
class RecipeController {
    private final RecipeService recipeService;

    RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping("/recipes")
    Mono<RecipeProjection> createRecipe(@RequestBody @Valid RecipeProjection recipe) {
        var user = ReactiveSecurityContextHolder.getContext().map(ctx -> ctx.getAuthentication());
        return user.map(u -> u.getName()).flatMap(name -> recipeService.createRecipe(recipe, name));
    }

    @GetMapping("/recipes")
    Flux<RecipeProjection> getRecipes() {
        var user = ReactiveSecurityContextHolder.getContext().map(ctx -> ctx.getAuthentication());
        return user.map(u -> u.getName()).flatMapMany(recipeService::getRecipes);
    }

    @GetMapping("/recipes/myRecipes/all")
    Flux<RecipeProjection> getMyRecipes() {
        var user = ReactiveSecurityContextHolder.getContext().map(ctx -> ctx.getAuthentication());
        return user.map(u -> u.getName()).flatMapMany(name -> recipeService.getMyRecipes(name));
    }

    @GetMapping("/recipes/myRecipes")
    Flux<RecipeProjection> getMyRecipesPaged(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        var user = ReactiveSecurityContextHolder.getContext().map(ctx -> ctx.getAuthentication());
        return user.map(u -> u.getName()).flatMapMany(name -> recipeService.getMyRecipesPaged(name, page, size));
    }
}
