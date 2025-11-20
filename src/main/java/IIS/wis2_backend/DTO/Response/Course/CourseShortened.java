package IIS.wis2_backend.DTO.Response.Course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseShortened {
    private Long id;
    private String name;
    private Double price;
    private String shortcut;
    private String completedBy;
}
