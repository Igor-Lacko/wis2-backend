package IIS.wis2_backend.DTO.Response.User;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * DTO for a user in admin view. Basically UserDTO + activated.
 */
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminUserDTO extends UserDTO {
    private boolean activated;
}
