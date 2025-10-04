package IIS.wis2_backend.DTO.User;

import IIS.wis2_backend.Models.User.WIS2User;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Data Transfer Object for User entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    /**
     * Constructor to create UserDTO from User entity.
     * 
     * @param user User entity.
     */
    public UserDTO(WIS2User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
    }
}