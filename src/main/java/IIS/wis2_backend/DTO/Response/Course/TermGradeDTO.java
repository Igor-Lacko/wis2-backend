package IIS.wis2_backend.DTO.Response.Course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TermGradeDTO {
    private Long termId;
    private String termName;
    private Integer points;
    private Integer maxPoints;
    private Boolean enrolled;
}
