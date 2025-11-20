package IIS.wis2_backend.DTO.Request.Term;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Data Transfer Object for an exam.
 */
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class ExamCreationDTO extends TermCreationDTO {
    @NotNull
    @DecimalMax("3")
    @DecimalMin("1")
    private Integer nofAttempt;
}