package IIS.wis2_backend.DTO.User;

import java.sql.Date;

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
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Date birthday;
    private String telephoneNumber;
}