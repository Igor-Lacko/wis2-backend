package IIS.wis2_backend.DTO.Request.Auth;

import java.sql.Date;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
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
    @Past
    private Date birthday;
}