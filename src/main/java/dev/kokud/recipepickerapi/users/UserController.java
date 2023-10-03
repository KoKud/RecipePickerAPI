package dev.kokud.recipepickerapi.users;

import dev.kokud.recipepickerapi.users.dto.UserDto;
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
    private final UserFacade userFacade;

    @PostMapping("/register")
    Mono<UserDto> registerUser(@RequestBody @Valid UserDto user) {
        var loggedUser = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return loggedUser.map(Principal::getName).flatMap(id -> userFacade.registerUser(id, user));
    }

    @PutMapping("/me")
    Mono<UserDto> updateUser(@RequestBody @Valid UserDto user) {
        var loggedUser = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return loggedUser.map(Principal::getName).flatMap(id -> userFacade.updateUser(id, user));
    }

    @GetMapping("/me")
    Mono<UserDto> getLoggedUser() {
        var loggedUser = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
        return loggedUser.map(Principal::getName).flatMap(userFacade::getUser);
    }
}
