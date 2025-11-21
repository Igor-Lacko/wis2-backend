package IIS.wis2_backend.DTO.Response.Course;

import java.time.LocalDateTime;

import IIS.wis2_backend.Enum.TermType;
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
    private TermType type;
    private Integer duration;
    private String roomShortcut;
    private Integer minPoints;
    private Integer maxPoints;
    private LocalDateTime date;
}
