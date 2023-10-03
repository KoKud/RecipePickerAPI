package dev.kokud.recipepickerapi.users;

import dev.kokud.recipepickerapi.users.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
class User {
    private String id;
    private String username;
    private String email;
    private String imageUri;
    private String imageUrl;   //used for Google account image
    private LocalDateTime banned;
    private Boolean autoShare;

    UserDto toDto(String serverUrl){
        return UserDto.builder()
                .withEmail(email)
                .withUsername(username)
                .withImageUrl(imageUri != null ? serverUrl + "file/" + imageUri : imageUrl)
                .withBanned(banned)
                .withAutoShare(autoShare)
                .build();
    }
}
