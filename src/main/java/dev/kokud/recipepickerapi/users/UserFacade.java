package dev.kokud.recipepickerapi.users;

import dev.kokud.recipepickerapi.users.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserFacade {
    private final UserRepository userRepository;
    private final String serverUrl;
    private final UserFactory userFactory;

    public Mono<UserDto> registerUser(String id, UserDto user) {
        return userRepository.findById(id)
                .switchIfEmpty(userRepository.insert(userFactory.from(user, id)))
                .map(userData -> userData.toDto(serverUrl));
    }

    public Mono<UserDto> getUser(String id) {
        return userRepository.findById(id).map(userData -> userData.toDto(serverUrl));
    }

    public Mono<UserDto> updateUser(String id, UserDto user) {
        return userRepository.findById(id)
                .switchIfEmpty(userRepository.insert(userFactory.from(user, id)))
                .map(userData ->{
                    if(user.getUsername() != null) userData.setUsername(user.getUsername());
                    if(user.getEmail() != null) userData.setEmail(user.getEmail());
                    if(user.getImageUrl() != null) userData.setImageUri(user.getImageUrl());
                    if(user.getAutoShare() != null) userData.setAutoShare(user.getAutoShare());
                    return userData;
                })
                .flatMap(userRepository::save)
                .map(userData -> userData.toDto(serverUrl));
    }
}
