package IIS.wis2_backend.Specifications;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import IIS.wis2_backend.Models.Course;

/**
 * Specification for filtering and sorting courses.
 */
public final class CourseSpecification {
    /**
     * Find by name or shortcut containing the query string (case-insensitive).
     * 
     * @param query the search query
     * @return a specification for filtering courses
     */
    public static Specification<Course> TitleOrShortcutContains(String query) {
        return (root, queryObj, criteriaBuilder) -> criteriaBuilder.or(
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")),
                "%" + query.toLowerCase() + "%"
            ),
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("shortcut")),
                "%" + query.toLowerCase() + "%"
            )
        );
    }

    /**
     * Finds courses by their end type.
     * 
     * @param courseEndType the end type to filter by
     * @return a specification for filtering courses
     */
    public static Specification<Course> EndedBy(String courseEndType) {
        return (root, queryObj, criteriaBuilder) -> criteriaBuilder.equal(
            root.get("completedBy"),
            courseEndType
        );
    }

    /**
     * Finds courses with price in the specified range.
     * 
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @return a specification for filtering courses
     */
    public static Specification<Course> PriceIsInRange(Double minPrice, Double maxPrice) {
        return (root, queryObj, criteriaBuilder) -> criteriaBuilder.between(
            root.get("price"),
            minPrice,
            maxPrice
        );
    }

    /**
     * Builds a Sort object based on the specified field and order.
     * 
     * @param sortBy  the field to sort by
     * @param reverse whether to sort in descending order
     * @return a Sort object
     */
    public static Sort BuildSort(String sortBy, boolean reverse) {
        sortBy = (sortBy == null || sortBy.isEmpty()) ? "name" : sortBy;
        Sort.Direction direction = reverse ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, sortBy);
    }
}