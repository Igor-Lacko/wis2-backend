package IIS.wis2_backend.DTO.Response.Course;

import IIS.wis2_backend.DTO.Response.User.UserShortened;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentGradeDTO {
    private UserShortened student;
    private Double grade;
}
