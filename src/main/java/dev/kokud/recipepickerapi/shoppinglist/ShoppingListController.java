package dev.kokud.recipepickerapi.shoppinglist;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping("/shopping-list")
@RequiredArgsConstructor
class ShoppingListController {
    private final ShoppingListService shoppingListService;

    @PostMapping("{ingredientId}")
    Mono<Boolean> modifyShoppingList(@PathVariable String ingredientId) {
        var user = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return user.map(Principal::getName).flatMap(name -> shoppingListService.modifyShoppingList(ingredientId, name));
    }

    @GetMapping
    Flux<ShoppingListProjection> getShoppingList() {
        var user = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return user.map(Principal::getName).flatMapMany(shoppingListService::getShoppingList);
    }
}
