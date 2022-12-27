package dev.kokud.recipepickerapi.recipes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@RestController
@RequiredArgsConstructor
class RecipeController {
    private final RecipeService recipeService;
    private final ReactiveGridFsTemplate gridFsTemplate;

    @PostMapping("/recipes")
    @ResponseStatus(HttpStatus.CREATED)
    Mono<RecipeProjection> createRecipe(@RequestBody @Valid RecipeProjection recipe) {
        var user = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return user.map(Principal::getName).flatMap(name -> recipeService.createRecipe(recipe, name));
    }
    @PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> handleFileUpload(@RequestPart("file") FilePart file) {
        return gridFsTemplate.store(file.content(), file.filename())
                .map(ObjectId::toHexString);
    }

    @DeleteMapping("/recipes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    Mono<Void> deleteRecipe(@PathVariable String id) {
        var user = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return user.map(Principal::getName).flatMap(name -> recipeService.deleteRecipe(name, id));
    }

    @GetMapping("/file/{id}")
    public Flux<Void> read(@PathVariable String id, ServerWebExchange exchange) {
        return this.gridFsTemplate.findOne(query(where("_id").is(id)))
                .flatMap(gridFsTemplate::getResource)
                .flatMapMany(r -> exchange.getResponse().writeWith(r.getDownloadStream()));
    }

    @GetMapping("/recipes")
    Flux<RecipeProjection> getPagedRecipes(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "ALL") RecipeCategory category) {
        var user = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return user.map(Principal::getName).flatMapMany(name -> recipeService.getRecipesPaged(name, category , page, size));
    }

    @GetMapping("recipes/{id}")
    Mono<RecipeProjection> getRecipesById(@PathVariable("id") String recipeId) {
        return recipeService.getRecipeById(recipeId);
    }

    @GetMapping("recipes/ingredient/{id}")
    Flux<RecipeProjection> getPagedRecipesByIngredientId(@PathVariable("id") String ingredientId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "ALL") RecipeCategory category) {
        var user = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return user.map(Principal::getName).flatMapMany(name -> recipeService.getRecipesPagedByIngredientId(name, ingredientId, category, page, size));
    }

    @GetMapping("/recipes/myRecipes")
    Flux<RecipeProjection> getMyRecipesPaged(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "ALL") RecipeCategory category) {
        var user = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return user.map(Principal::getName).flatMapMany(name -> recipeService.getMyRecipesPaged(name, category, page, size));
    }
}
