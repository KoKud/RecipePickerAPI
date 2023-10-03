package dev.kokud.recipepickerapi.shoppinglist;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
class ShoppingListService {
    private final ShoppingListRepository shoppingListRepository;

    public Mono<Boolean> modifyShoppingList(String ingredientId, String name){
        return shoppingListRepository.findByIngredientIdAndUserId(ingredientId, name)
                .flatMap(shoppingList -> shoppingListRepository.delete(shoppingList).thenReturn(false))
                .switchIfEmpty(shoppingListRepository.insert(new ShoppingList(null, name, ingredientId)).thenReturn(true));
    }

    public Flux<ShoppingListProjection> getShoppingList(String name){
        return shoppingListRepository.findByUserId(name).map(ShoppingListProjection::new);
    }
}
