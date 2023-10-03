package dev.kokud.recipepickerapi.users.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mongodb.lang.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Null;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonDeserialize(builder = UserDto.Builder.class)
public class UserDto {
    public static Builder builder() {
        return new Builder();
    }

    @Nullable
    @Email(message = "Email should be valid")
    private String email;
    private String username;
    private String imageUrl;
    @Null
    private LocalDateTime banned;
    private Boolean autoShare;

    public static class Builder {
        private String email;
        private String username;
        private String imageUrl;
        private LocalDateTime banned;
        private Boolean autoShare;

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder withBanned(LocalDateTime banned) {
            this.banned = banned;
            return this;
        }

        public Builder withAutoShare(Boolean autoShare) {
            this.autoShare = autoShare;
            return this;
        }

        public UserDto build() {
            return new UserDto(email, username, imageUrl, banned, autoShare);
        }
    }
}
