package IIS.wis2_backend.DTO.User;

import java.sql.Date;

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
    private String email;

    @NotNull
    @NotEmpty
    private String password;

    @NotNull
    @NotEmpty
    private Date birthday;
}