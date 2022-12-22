package dev.kokud.recipepickerapi.recipes;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
interface RecipeRepository extends ReactiveMongoRepository<Recipe, String> {
    Flux<Recipe> findByCreatorId(String creatorId);

    @Query("{$or: [{creatorId: ?0 }," +
            "   {$and: [" +
            "        {banned:false}," +
            "        {shared:false}" +
            "]}]}")
    Flux<Recipe> findAll(@Param("id") String creatorId);

    @Query("{$or: [{creatorId: ?0 }," +
            "   {$and: [" +
            "        {\"ingredients.id\" : ?1}," +
            "        {banned:false}," +
            "        {shared:false}" +
            "]}]}")
    Flux<Recipe> findByIngredientId(String id, String user);
}
