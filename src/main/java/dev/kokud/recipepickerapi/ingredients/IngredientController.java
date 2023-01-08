package dev.kokud.recipepickerapi.ingredients;

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
@RequestMapping("/ingredients")
@RequiredArgsConstructor
class IngredientController {
    private final IngredientService ingredientService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Mono<IngredientProjection> createIngredient(@RequestBody @Valid IngredientProjection ingredient) {
        var user = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return user.map(Principal::getName).flatMap(name -> ingredientService.createIngredient(ingredient, name));
    }

    @GetMapping
    Flux<IngredientProjection> getPagedIngredients(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "ALL") IngredientCategory category, @RequestParam(defaultValue = "") String search) {
        var user = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return user.map(Principal::getName).flatMapMany(name -> ingredientService.getIngredientsPaged(name, category, search, page, size));
    }

    @GetMapping("{id}")
    Mono<IngredientProjection> getIngredientById(@PathVariable("id") String ingredientId) {
        return ingredientService.getIngredientById(ingredientId);
    }
}
