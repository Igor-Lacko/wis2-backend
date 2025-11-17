package IIS.wis2_backend.DTO.Request.ModelAttributes;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

/**
 * Class used as a @ModelAttribute for filtering courses in CourseController.
 */
@Data
public class CourseFilter {
    /**
     * Whether to reverse the sorting order.
     */
    private boolean reverse = false;

    /**
     * Attribute to sort by. Defaults to "name", also supports "price".
     */
    private String sortBy = "name";

    /**
     * Whether to show courses that end by exam.
     */
    private boolean endedByExam = true;

    /**
     * Whether to show courses that end by unit credit.
     */
    private boolean endedByUnitCredit = true;

    /**
     * Whether to show courses that end by graded unit credit.
     */
    private boolean endedByGradedUnitCredit = true;

    /**
     * Whether to show courses that end by both unit credit and exam.
     */
    private boolean endedByBoth = true;

    /**
     * Search query to filter courses by name or description.
     */
    private String query;

    @DecimalMin(value = "0.0", inclusive = true, message = "minPrice must be non-negative")
    private Double minPrice;

    @DecimalMin(value = "0.0", inclusive = true, message = "maxPrice must be non-negative")
    private Double maxPrice;
}