package IIS.wis2_backend.DTO.Request.Room;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfficeAssignmentRequest {
    private List<Long> userIds;
}
