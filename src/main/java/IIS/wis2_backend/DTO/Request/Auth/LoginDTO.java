package IIS.wis2_backend.DTO.Request.Auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data transfer object sent to the backend when a user tries to log in.
 */
@Data
public class LoginDTO {
    @NotNull
    @NotEmpty
    private String password;

    @NotNull
    @NotEmpty
    private String username;
}