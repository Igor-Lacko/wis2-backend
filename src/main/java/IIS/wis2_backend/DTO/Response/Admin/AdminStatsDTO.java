package IIS.wis2_backend.DTO.Response.Admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminStatsDTO {
    private Long totalUsers;
    private Long totalCourses;
    private Long pendingCourseRequests;
    private Long pendingRoomRequests;
}
