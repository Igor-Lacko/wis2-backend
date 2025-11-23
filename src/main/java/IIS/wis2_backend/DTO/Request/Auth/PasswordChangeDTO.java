package IIS.wis2_backend.DTO.Request.Auth;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for changing a user's password.
 */
@Data
public class PasswordChangeDTO {
    @NotBlank
    private String password;

    @NotBlank
    private String confirmPassword;

    /**
     * Check if password and confirmPassword match.
     */
    @JsonIgnore
    @AssertTrue
    public boolean isPasswordsMatching() {
        return password != null && password.equals(confirmPassword);
    }
}
