package IIS.wis2_backend.DTO.Request.Term;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.SuperBuilder;

/**
 * Generic term DTO.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class TermCreationDTO {
    @NotNull
    private Integer minPoints;

    @NotNull
    private Integer maxPoints;

    @NotNull
    private LocalDateTime date;

    @NotNull
    private Integer duration;

    @NotNull
    private String description;

    @NotNull
    private String name;

    @NotNull
    private Boolean mandatory;

    @NotNull
    private Boolean autoRegistration;

    @NotNull
    private Long CourseID;

    @NotNull
    private Long SupervisorID;

    @NotNull
    private Set<Long> RoomIDs;
}