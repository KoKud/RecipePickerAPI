package dev.kokud.recipepickerapi.recipes.favorite;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
class FavoriteController {
    private final FavoriteService favoriteService;

    @PostMapping("/recipes/{id}/favorite")
    Mono<?> changeFavoriteStatusForRecipe(@PathVariable String id) {
        var user = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return user.map(Principal::getName).flatMap(name -> favoriteService.updateFavoriteStatusForRecipe(id, name));
    }
}
