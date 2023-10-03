package dev.kokud.recipepickerapi.favorite;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping("/favorites/recipes")
@RequiredArgsConstructor
class FavoriteController {
    private final FavoriteService favoriteService;

    @PostMapping("{id}")
    Mono<Boolean> changeFavoriteStatusForRecipe(@PathVariable String id) {
        var user = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return user.map(Principal::getName).flatMap(name -> favoriteService.updateFavoriteStatusForRecipe(id, name));
    }

    @GetMapping
    Flux<FavoriteProjection> getFavoriteRecipes() {
        var user = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return user.map(Principal::getName).flatMapMany(favoriteService::getFavoriteRecipes);
    }
}
