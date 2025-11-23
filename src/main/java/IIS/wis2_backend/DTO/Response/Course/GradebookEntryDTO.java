package IIS.wis2_backend.DTO.Response.Course;

import java.util.List;

import IIS.wis2_backend.DTO.Response.User.UserShortened;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GradebookEntryDTO {
    private UserShortened student;
    private List<TermGradeDTO> termGrades;
    private Integer points;
    private Boolean unitCredit;
    private Boolean examPassed;
    private Double finalGrade;
    private Boolean completed;
}
