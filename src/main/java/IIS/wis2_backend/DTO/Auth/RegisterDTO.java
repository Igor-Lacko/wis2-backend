package IIS.wis2_backend.DTO.Auth;

import java.sql.Date;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;



/**
 * DTO sent to the backend when a user tries to register.
 */
@Data
public class RegisterDTO {
    @NotNull
    @NotEmpty
    private String firstName;

    @NotNull
    @NotEmpty
    private String lastName;

    @NotNull
    @NotEmpty
    @Email
    private String email;

    @NotNull
    @NotEmpty
    private String password;

    @NotNull
    private Date birthday;
}