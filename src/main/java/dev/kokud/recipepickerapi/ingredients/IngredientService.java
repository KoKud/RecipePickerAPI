package dev.kokud.recipepickerapi.ingredients;

import dev.kokud.recipepickerapi.ownedingredients.OwnedIngredientProjection;
import dev.kokud.recipepickerapi.ownedingredients.OwnedIngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final OwnedIngredientService ownedIngredientService;

    public Mono<String> createIngredient(IngredientDto ingredientDto, String name) {
        var ingredient = ingredientDto.toIngredient();
        ingredient.setCreatorId(name);
        return ingredientRepository.findByName(ingredient.getName())
                .flatMap(ingredient1 -> {
                    if(Boolean.FALSE.equals(ingredient1.getShared()))ingredient1.setShared(true);
                    else return Mono.just(ingredient1);
                    return ingredientRepository.save(ingredient1);
                })
                .switchIfEmpty(ingredientRepository.insert(ingredient))
                .map(Ingredient::getId);
    }

    Flux<IngredientDto> getIngredientsPaged(String name, IngredientCategory category, String search, int page, int size) {
        if(category == IngredientCategory.OWNED) return ownedIngredientService.getOwnedIngredients(name)
                .map(OwnedIngredientProjection::getIngredientId)
                .skip((long) page * size).take(size)
                .collectList().flatMapMany(ingredientRepository::findByIdIn)
                .map(IngredientDto::new);

        return ingredientRepository.findAll(name)
                .filter(ingredient -> category == IngredientCategory.ALL || Optional.ofNullable(ingredient.getCategories()).orElse(List.of()).contains(category))
                .filter(ingredient -> ingredient.getName().toLowerCase().contains(search.toLowerCase()))
                .skip((long) page * size).take(size).map(IngredientDto::new);
    }

    public Mono<IngredientDto> getIngredientById(String ingredientId) {
        return ingredientRepository.findById(ingredientId)
                .map(IngredientDto::new);
    }
}
