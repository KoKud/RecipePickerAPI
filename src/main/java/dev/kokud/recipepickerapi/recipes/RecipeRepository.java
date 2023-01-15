package dev.kokud.recipepickerapi.recipes;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
interface RecipeRepository extends ReactiveMongoRepository<Recipe, String> {
    Flux<Recipe> findByCreatorId(String creatorId);

    Flux<Recipe> findByIdInAndCreatorId(List<String> id, String creatorId);

    @Query("{$or: [{creatorId: ?0 }," +
            "   {$and: [" +
            "        {banned:false}," +
            "        {shared:true}" +
            "]}]}")
    Flux<Recipe> findAll(@Param("id") String creatorId);

    @Query("{$and: [{\"ingredients._id\" : ?1 }," +
            "       {$or : [{$and: [{banned:false}," +
            "       {shared:true}]}," +
            "       {creatorId: ?0 }]}" +
            "]}")
    Flux<Recipe> findByIngredientsId(String name, String id);

    Flux<Recipe> findByIdIn(List<String> strings);

    Flux<Recipe> findByIdInAndIngredientsId(List<String> ids, String id);
}
