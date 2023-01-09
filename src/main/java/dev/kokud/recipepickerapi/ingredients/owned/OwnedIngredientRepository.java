package dev.kokud.recipepickerapi.ingredients.owned;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
interface OwnedIngredientRepository extends ReactiveMongoRepository<OwnedIngredient, String> {

    Mono<OwnedIngredient> findByIngredientIdAndUserId(String id, String name);

    Flux<OwnedIngredient> findByUserId(String userId);
}
