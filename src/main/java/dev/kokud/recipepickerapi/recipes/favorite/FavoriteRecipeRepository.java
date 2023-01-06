package dev.kokud.recipepickerapi.recipes.favorite;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

interface FavoriteRecipeRepository extends ReactiveMongoRepository<Favorite, String> {

    Mono<Favorite> findByRecipeIdAndUserId(String id, String name);

    Flux<Favorite> findByUserId(String s);
}
