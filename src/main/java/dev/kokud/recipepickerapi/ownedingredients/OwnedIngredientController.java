package dev.kokud.recipepickerapi.ownedingredients;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping("/ingredients/owned")
@RequiredArgsConstructor
class OwnedIngredientController {
    private final OwnedIngredientService ownedIngredientService;

    @PostMapping
    Mono<OwnedIngredientProjection> changeOwnedIngredientAmount(@RequestBody @Valid OwnedIngredientProjection ownedIngredient) {
        var user = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return user.map(Principal::getName)
                .flatMap(name -> ownedIngredientService.updateOwnedIngredientAmount(ownedIngredient, name));
    }

    @GetMapping
    Flux<OwnedIngredientProjection> getOwnedIngredients() {
        var user = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return user.map(Principal::getName).flatMapMany(ownedIngredientService::getOwnedIngredients);
    }
}
