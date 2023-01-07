package dev.kokud.recipepickerapi.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

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
    private Date banned;
    private Boolean autoShare;
}
