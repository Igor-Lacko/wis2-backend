package IIS.wis2_backend.DTO.Request.Term;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import IIS.wis2_backend.Enum.TermType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
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
    @DecimalMin("0")
    private Integer minPoints;

    @DecimalMin("0")
    private Integer maxPoints;

    @Future
    @NotNull
    private LocalDateTime startDate;

    @NotNull
    @DecimalMin("0")
    private Integer duration;

    private String description;

    @NotBlank
    private String name;

    @NotNull
    private Boolean autoregister;

    @NotBlank
    private String roomShortcut;

    @NotNull
    private TermType type;

    /**
     * Validates that the maxPoints is greater than or equal to minPoints.
     */
    @JsonIgnore
    @AssertTrue(message = "maxPoints must be greater than or equal to minPoints")
    public boolean isMaxPointsValid() {
        if (maxPoints == null || minPoints == null) {
            return true;
        }
        return maxPoints >= minPoints;
    }

    /**
     * Validates that if the type is not LECTURE, then maxPoints and minPoints must be provided.
     */
    @JsonIgnore
    @AssertTrue(message = "maxPoints and minPoints must be provided for non-LECTURE terms")
    public boolean isPointsProvidedForNonLecture() {
        if (type == TermType.LECTURE) {
            return true;
        }
        return maxPoints != null && minPoints != null;
    }
}