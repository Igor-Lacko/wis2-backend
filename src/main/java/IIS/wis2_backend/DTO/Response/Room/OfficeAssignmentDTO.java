package IIS.wis2_backend.DTO.Response.Room;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfficeAssignmentDTO {
    private Long officeId;
    private String officeShortcut;
    private List<Long> assignedUserIds;
}
