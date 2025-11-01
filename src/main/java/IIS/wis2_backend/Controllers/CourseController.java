package IIS.wis2_backend.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import IIS.wis2_backend.DTO.Course.FullCourseDTO;
import IIS.wis2_backend.DTO.Course.LightweightCourseDTO;
import IIS.wis2_backend.Services.CourseService;

/**
 * Controller for course access.
 */
@RestController
@RequestMapping("/courses")
public class CourseController {
    /**
     * Course service.
     */
    private final CourseService courseService;

    /**
     * Constructor for CourseController.
     * 
     * @param courseService Course service.
     */
    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * Getter for all courses.
     * 
     * @param reverse  Whether to reverse the order.
     * @param query    Search query.
     * @param sortBy   Field to sort by.
     * @param endedBy  Filter by end type.
     * @param minPrice Minimum price filter.
     * @param maxPrice Maximum price filter.
     * @return list of all courses matching the criteria.
     */
    @GetMapping
    public List<LightweightCourseDTO> GetAllCourses(
        @RequestParam(defaultValue = "false") boolean reverse,
        @RequestParam(required = false) String query,
        @RequestParam(required = false) String sortBy,
        @RequestParam(required = false) String endedBy,
        @RequestParam(required = false) Double minPrice,
        @RequestParam(required = false) Double maxPrice
    ) {
        return courseService.GetAllCourses(reverse, query, sortBy, endedBy, minPrice, maxPrice);
    }

    /**
     * Getter for a course by id.
     * 
     * @param id Course id.
     */
    @GetMapping("/{id}")
    public FullCourseDTO GetCourseById(@PathVariable Long id) {
        return courseService.GetCourseById(id);
    }
}