package dev.kokud.recipepickerapi.shoppinglist;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

interface ShoppingListRepository extends ReactiveMongoRepository<ShoppingList, String> {
    Flux<ShoppingList> findByUserId(String id);

    Mono<ShoppingList> findByIngredientIdAndUserId(String ingredientId, String name);
}
