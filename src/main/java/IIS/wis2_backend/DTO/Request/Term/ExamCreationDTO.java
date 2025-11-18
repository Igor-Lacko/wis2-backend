package IIS.wis2_backend.DTO.Request.Term;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
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
    @NonNull
    @DecimalMax("3")
    @DecimalMin("1")
    private Integer nofAttempt;
}