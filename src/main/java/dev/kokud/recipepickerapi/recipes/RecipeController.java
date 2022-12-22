package dev.kokud.recipepickerapi.recipes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@RestController
@RequiredArgsConstructor
class RecipeController {
    private final RecipeService recipeService;
    private final ReactiveGridFsTemplate gridFsTemplate;

    @PostMapping("/recipes")
    Mono<RecipeProjection> createRecipe(@RequestBody @Valid RecipeProjection recipe) {
        var user = ReactiveSecurityContextHolder.getContext().map(ctx -> ctx.getAuthentication());
        return user.map(u -> u.getName()).flatMap(name -> recipeService.createRecipe(recipe, name));
    }
    @PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> handleFileUpload(@PathVariable String id, @RequestPart("file") FilePart file) {
        System.out.println(file.filename());
        return gridFsTemplate.store(file.content(), file.filename())
                .map(id1 -> id1.toHexString());
    }

    @GetMapping("/file/{id}")
    public Flux<Void> read(@PathVariable String id, ServerWebExchange exchange) {
        return this.gridFsTemplate.findOne(query(where("_id").is(id)))
                .flatMap(gridFsTemplate::getResource)
                .flatMapMany(r -> exchange.getResponse().writeWith(r.getDownloadStream()));
    }

    @GetMapping("/recipes")
    Flux<RecipeProjection> getPagedRecipes(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "ALL") RecipeCategory category) {
        var user = ReactiveSecurityContextHolder.getContext().map(ctx -> ctx.getAuthentication());
        return user.map(u -> u.getName()).flatMapMany(name -> recipeService.getRecipesPaged(name, category , page, size));
    }

    @GetMapping("recipes/{id}")
    Flux<RecipeProjection> getPagedRecipesByIngredientId(@PathVariable String id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "ALL") RecipeCategory category) {
        var user = ReactiveSecurityContextHolder.getContext().map(ctx -> ctx.getAuthentication());
        return user.map(u -> u.getName()).flatMapMany(name -> recipeService.getRecipesPagedByIngredientId(id, name, category, page, size));
    }

    @GetMapping("/recipes/myRecipes")
    Flux<RecipeProjection> getMyRecipesPaged(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "ALL") RecipeCategory category) {
        var user = ReactiveSecurityContextHolder.getContext().map(ctx -> ctx.getAuthentication());
        return user.map(u -> u.getName()).flatMapMany(name -> recipeService.getMyRecipesPaged(name, category, page, size));
    }
}
