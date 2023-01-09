package dev.kokud.recipepickerapi.ingredients;

import dev.kokud.recipepickerapi.ingredients.owned.OwnedIngredientProjection;
import dev.kokud.recipepickerapi.ingredients.owned.OwnedIngredientService;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final OwnedIngredientService ownedIngredientService;

    public Mono<IngredientProjection> createIngredient(IngredientProjection ingredientProjection, String name) {
        var ingredient = ingredientProjection.toIngredient();
        ingredient.setCreatorId(name);
        return ingredientRepository.findByName(ingredient.getName())
                .flatMap(ingredient1 -> {
                    if(Boolean.FALSE.equals(ingredient1.getShared()))ingredient1.setShared(true);
                    else return Mono.just(ingredient1);
                    return ingredientRepository.save(ingredient1);
                })
                .switchIfEmpty(ingredientRepository.insert(ingredient))
                .map(IngredientProjection::new);
    }

    public Publisher<IngredientProjection> getIngredientsPaged(String name, IngredientCategory category, String search, int page, int size) {
        if(category == IngredientCategory.OWNED) return ownedIngredientService.getOwnedIngredients(name)
                .map(OwnedIngredientProjection::getIngredientId)
                .skip((long) page * size).take(size)
                .collectList().flatMapMany(ingredientRepository::findByIdIn)
                .map(IngredientProjection::new);

        return ingredientRepository.findAll(name)
                .filter(ingredient -> category == IngredientCategory.ALL || Optional.ofNullable(ingredient.getCategories()).orElse(new ArrayList<>()).contains(category))
                .filter(ingredient -> ingredient.getName().toLowerCase().contains(search.toLowerCase()))
                .skip((long) page * size).take(size).map(IngredientProjection::new);
    }

    public Mono<IngredientProjection> getIngredientById(String ingredientId) {
        return ingredientRepository.findById(ingredientId)
                .map(IngredientProjection::new);
    }
}
