package IIS.wis2_backend.DTO.User;


import lombok.Builder;
import lombok.Data;

/**
 * DTO for the response after a user registers.
 */
@Builder
@Data
public class RegisterResponseDTO {
    private Long id;
    private String username;
}