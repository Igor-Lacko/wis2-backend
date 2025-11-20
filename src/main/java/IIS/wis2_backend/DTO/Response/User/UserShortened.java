package IIS.wis2_backend.DTO.Response.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserShortened {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
}
