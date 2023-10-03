package dev.kokud.recipepickerapi.ownedingredients;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OwnedIngredientService {
    private final OwnedIngredientRepository ownedIngredientRepository;

    public Mono<OwnedIngredientProjection> updateOwnedIngredientAmount(OwnedIngredientProjection ownedIngredient, String name) {
        if(ownedIngredient.getAmount() <=0) return ownedIngredientRepository
                .findByIngredientIdAndUserId(ownedIngredient.getIngredientId(), name)
                .flatMap(ownedIngredientRepository::delete)
                .map(data -> ownedIngredient);

        return ownedIngredientRepository.findByIngredientIdAndUserId(ownedIngredient.getIngredientId(), name)
                .flatMap(owned -> {
                    owned.setAmount(ownedIngredient.getAmount());
                    owned.setType(ownedIngredient.getType());
                    return ownedIngredientRepository.save(owned);
                })
                .switchIfEmpty(ownedIngredientRepository.insert(ownedIngredient.toOwnedIngredient(name)))
                .map(OwnedIngredientProjection::new);
    }

    public Flux<OwnedIngredientProjection> getOwnedIngredients(String userId) {
        return ownedIngredientRepository.findByUserId(userId).map(OwnedIngredientProjection::new);
    }
}
