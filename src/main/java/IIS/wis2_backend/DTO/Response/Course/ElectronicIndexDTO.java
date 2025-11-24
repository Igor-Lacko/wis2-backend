package IIS.wis2_backend.DTO.Response.Course;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * DTO representing the user's electronic index.
 */
@AllArgsConstructor
@Data
@Builder
public class ElectronicIndexDTO {
    private List<ElectronicIndexItemDTO> courses;
    private Double gpa;
}