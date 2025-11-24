package IIS.wis2_backend.DTO.Response.Course;

import IIS.wis2_backend.Enum.CourseEndType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * DTO of one course in the user's electronic index (basically VUT app equivalent).
 */
@Data
@AllArgsConstructor
@Builder
public class ElectronicIndexItemDTO {
    private String courseName;
    private String courseShortcut;
    private Boolean hasUnitCredit;
    private Boolean examPassed;
    private Boolean hasFailed;
    private Double finalGrade;
    private Integer points;
    private CourseEndType courseEndType;
}