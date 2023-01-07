package dev.kokud.recipepickerapi.users;

import com.mongodb.lang.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Null;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
class UserProjection {
    @Nullable
    @Email(message = "Email should be valid")
    private String email;
    private String username;
    private String imageUrl;   //used for Google account image
    @Null
    private Date banned;
    private Boolean autoShare;

    public UserProjection(User user, String serverUrl) {
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.imageUrl = user.getImageUri() != null ? serverUrl + "file/" + user.getImageUri() : user.getImageUrl();
        this.banned = user.getBanned();
        this.autoShare = user.getAutoShare();
    }

    public User toUser(String id) {
        return new User(
                id,
                username,
                email,
                null,
                imageUrl,
                banned,
                autoShare
        );
    }
}
