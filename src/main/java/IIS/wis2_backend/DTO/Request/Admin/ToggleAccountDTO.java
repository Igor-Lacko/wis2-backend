package IIS.wis2_backend.DTO.Request.Admin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for toggling a user account's activation status.
 */
@Data
public class ToggleAccountDTO {
    @NotNull
    private Long userId;
}
