package IIS.wis2_backend.DTO.Request.Term;

import java.time.LocalDateTime;

import IIS.wis2_backend.Enum.TermType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.DecimalMin;
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
    @NotNull
    @DecimalMin("0")
    private Integer minPoints;

    @NotNull
    @DecimalMin("0")
    private Integer maxPoints;

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
}