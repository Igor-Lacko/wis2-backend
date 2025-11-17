package IIS.wis2_backend.DTO.Request.Term;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Generic term DTO.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class TermCreationDTO {
    private Integer minPoints;
    private Integer maxPoints;
    private String date;
    private Integer duration;
    private String description;
    private String name;
    private Boolean mandatory;
}