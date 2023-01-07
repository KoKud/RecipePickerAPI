package dev.kokud.recipepickerapi.users;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
class UserService {
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
}
