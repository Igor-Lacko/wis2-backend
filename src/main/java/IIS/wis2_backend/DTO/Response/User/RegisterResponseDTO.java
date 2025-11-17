package IIS.wis2_backend.DTO.Response.User;


import lombok.Builder;
import lombok.Data;

/**
 * DTO for the response after a user registers.
 */
@Builder
@Data
public class RegisterResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}