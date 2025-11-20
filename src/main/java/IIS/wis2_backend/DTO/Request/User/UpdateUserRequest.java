package IIS.wis2_backend.DTO.Request.User;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private Date birthday;
    private String telephoneNumber;
}
