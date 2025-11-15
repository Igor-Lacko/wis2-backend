package IIS.wis2_backend.DTO.Auth;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for responses to be sent back to the user after login.
 */
@AllArgsConstructor
@Data
public class SuccesfulLoginDTO {
    private Long id;
    private String username;
    private String role;
}