package dev.kokud.recipepickerapi.users;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final String serverUrl;

    public Mono<UserProjection> registerUser(String id, UserProjection user) {
        return userRepository.findById(id)
                .switchIfEmpty(userRepository.insert(user.toUser(id)))
                .map(userData -> new UserProjection(userData, serverUrl));
    }

    public Mono<UserProjection> getUser(String id) {
        return userRepository.findById(id).map(userData -> new UserProjection(userData, serverUrl));
    }

    public Mono<UserProjection> updateUser(String id, UserProjection user) {
        return userRepository.findById(id)
                .switchIfEmpty(userRepository.insert(user.toUser(id)))
                .map(userData ->{
                    if(user.getUsername() != null) userData.setUsername(user.getUsername());
                    if(user.getEmail() != null) userData.setEmail(user.getEmail());
                    if(user.getImageUrl() != null) userData.setImageUri(user.getImageUrl());
                    if(user.getAutoShare() != null) userData.setAutoShare(user.getAutoShare());
                    return userData;
                })
                .flatMap(userRepository::save)
                .map(userData -> new UserProjection(userData, serverUrl));
    }
}
