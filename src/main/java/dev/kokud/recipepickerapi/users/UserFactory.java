package dev.kokud.recipepickerapi.users;

import dev.kokud.recipepickerapi.users.dto.UserDto;
import org.springframework.stereotype.Service;

@Service
class UserFactory {
    User from(final UserDto source, final String id) {
        return new User(
                id,
                source.getUsername(),
                source.getEmail(),
                source.getImageUrl(),
                source.getImageUrl(),
                null,
                source.getAutoShare()
        );
    }
}
