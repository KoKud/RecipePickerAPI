package dev.kokud.recipepickerapi.users;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
class UserController {
    private final UserService userService;

    @PostMapping("/register")
    Mono<UserProjection> registerUser(@RequestBody @Valid UserProjection user) {
        var loggedUser = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return loggedUser.map(Principal::getName).flatMap(id -> userService.registerUser(id, user));
    }

    @PutMapping("/me")
    Mono<UserProjection> updateUser(@RequestBody @Valid UserProjection user) {
        var loggedUser = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return loggedUser.map(Principal::getName).flatMap(id -> userService.updateUser(id, user));
    }

    @GetMapping("/me")
    Mono<UserProjection> getLoggedUser() {
        var loggedUser = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return loggedUser.map(Principal::getName).flatMap(userService::getUser);
    }
}
