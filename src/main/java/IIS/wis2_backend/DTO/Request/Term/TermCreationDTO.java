package IIS.wis2_backend.DTO.Request.Term;

import java.time.LocalDateTime;

import IIS.wis2_backend.Enum.TermType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    private Integer minPoints;

    @NotNull
    private Integer maxPoints;

    @NotNull
    private LocalDateTime date;

    @NotNull
    private Integer duration;

    private String description;

    @NotBlank
    private String name;

    @NotNull
    private Boolean autoRegistration;

    @NotBlank
    private String courseShortcut;

    @NotBlank
    private String roomShortcut;

    @NotNull
    private TermType type;
}