package dev.kokud.recipepickerapi.recipes.favorite;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

interface FavoriteRecipeRepository extends ReactiveMongoRepository<Favorite, String> {

    Mono<Favorite> findByRecipeIdAndUserId(String id, String name);
}
