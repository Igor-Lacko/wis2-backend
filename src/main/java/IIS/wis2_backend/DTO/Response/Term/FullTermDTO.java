package IIS.wis2_backend.DTO.Response.Term;

import java.time.LocalDateTime;

import IIS.wis2_backend.Enum.TermType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class FullTermDTO {
    @NotEmpty
    private String name;

    private String description;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @NotEmpty
    private String roomShortcut;

    @NotEmpty
    private String courseName;

    @NotNull
    private Integer nofEnrolled;

    @NotNull
    private Integer capacity;

    @NotNull
    private Boolean autoregister;

    @NotNull
    private TermType termType;

    private Integer minPoints;

    private Integer maxPoints;

    private Boolean isRegistered;
}