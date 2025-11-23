package IIS.wis2_backend.DTO.Response.Course;

import com.fasterxml.jackson.annotation.JsonProperty;
import IIS.wis2_backend.Enum.CourseEndType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * DTO representing a registered course list item.
 */
@AllArgsConstructor
@Data
@Builder
public class RegisteredCourseListItemDTO {
    @NotNull
    private Long id;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    private Double price;

    @NotNull
    @NotEmpty
    private String shortcut;

    @NotNull
    @NotEmpty
    private CourseEndType completedBy;

    @NotNull
    @JsonProperty("isSupervisor")
    private boolean isSupervisor;

    @NotNull
    @JsonProperty("isTeacher")
    private boolean isTeacher;

    @NotNull
    @JsonProperty("isStudent")
    private boolean isStudent;

    // TODO
    @JsonProperty("hasRequested")
    private Boolean hasRequested;
}
