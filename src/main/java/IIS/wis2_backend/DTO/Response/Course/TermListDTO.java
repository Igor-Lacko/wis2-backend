package IIS.wis2_backend.DTO.Response.Course;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TermListDTO {
    private Long id;
    private String name;
    private String type;
    private Integer maxPoints;
    private LocalDateTime date;
}
