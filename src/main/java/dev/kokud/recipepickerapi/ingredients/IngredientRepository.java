package dev.kokud.recipepickerapi.ingredients;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
interface IngredientRepository extends ReactiveMongoRepository<Ingredient, String> {

    @Query("{$or: [{creatorId: ?0 }," +
            "   {$and: [" +
            "        {banned:false}," +
            "        {shared:true}" +
            "]}]}")
    Flux<Ingredient> findAll(@Param("id") String creatorId);

    Mono<Ingredient> findByName(String name);
}
