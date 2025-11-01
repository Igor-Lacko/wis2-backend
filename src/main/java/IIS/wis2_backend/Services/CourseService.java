package IIS.wis2_backend.Services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import IIS.wis2_backend.DTO.Course.FullCourseDTO;
import IIS.wis2_backend.DTO.Course.LightweightCourseDTO;
import IIS.wis2_backend.Exceptions.ExceptionTypes.NotFoundException;
import IIS.wis2_backend.Models.Course;
import IIS.wis2_backend.Models.User.Teacher;
import IIS.wis2_backend.Repositories.CourseRepository;
import IIS.wis2_backend.Specifications.CourseSpecification;

/**
 * Service for managing courses.
 */
@Service
public class CourseService {
    /**
     * Course repository.
     */
    private final CourseRepository courseRepository;

    /**
     * Constructor for CourseService.
     * 
     * @param courseRepository the course repository
     */
    @Autowired
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    /**
     * Getter for all courses. Returns lightweight DTOs.
     * 
     * @param reverse whether to reverse the order
     * @param query   search query to filter courses
     * @param sortBy  field to sort by
     * @param filterBy field to filter by
     * @param filterValue value to filter by
     * @return a list of all courses
     */
    public List<LightweightCourseDTO> GetAllCourses(
        boolean reverse,
        String query,
        String sortBy,
        String endedBy,
        Double minPrice,
        Double maxPrice
    ) {
        if (sortBy != "name" && sortBy != "price" && !sortBy.isEmpty()) {
            throw new IllegalArgumentException("Invalid sortBy parameter!");
        }

        Specification<Course> spec = query != null
            ? CourseSpecification.TitleOrShortcutContains(query)
            : null;

        // Filter by end type
        if (endedBy != null) {
            spec = spec == null
                ? CourseSpecification.EndedBy(endedBy)
                : spec.and(CourseSpecification.EndedBy(endedBy));
        }

        // Filter by price range
        if (minPrice != null || maxPrice != null) {
            Double min = minPrice != null ? minPrice : 0.0;
            Double max = maxPrice != null ? maxPrice : Double.MAX_VALUE;

            // Check for invalid args
            if (min < 0 || max < 0 || min > max) {
                throw new IllegalArgumentException("Invalid price range!");
            }

            spec = spec == null
                ? CourseSpecification.PriceIsInRange(min, max)
                : spec.and(CourseSpecification.PriceIsInRange(min, max));
        }

        List<Course> courses = courseRepository.findAll(
            spec,
            CourseSpecification.BuildSort(sortBy, reverse)
        );

        return courses.stream()
                .map(this::CourseToLightweightDTO)
                .collect(Collectors.toList());
    }

    /**
     * Getter for a course by id. Returns full DTO.
     * 
     * @param id the course id
     * @return the course with the given id
     * @throws IllegalArgumentException if the course with the given id does not exist
     */
    public FullCourseDTO GetCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("The course with this id doesn't exist!"));
        return CourseToFullDTO(course);
    }

    /**
     * Utility method to convert Course to LightweightCourseDTO.
     * 
     * @param course The course to convert
     * @return The corresponding LightweightCourseDTO
     */
    private LightweightCourseDTO CourseToLightweightDTO(Course course) {
        return new LightweightCourseDTO(course.getId(), course.getName(), course.getPrice(), course.getShortcut());
    }

    /**
     * Utility method to convert Course to FullCourseDTO.
     * 
     * @param course The course to convert
     * @return The corresponding FullCourseDTO
     */
    private FullCourseDTO CourseToFullDTO(Course course) {
        return new FullCourseDTO(
                course.getId(),
                course.getName(),
                course.getPrice(),
                course.getDescription(),
                course.getShortcut(),
                GetSupervisorName(course),
                GetTeacherNames(course)
        );
    }

    /**
     * Utility method to get the supervisor's name of a course.
     * 
     * @param course The course
     * @return The supervisor's full name
     */
    private String GetSupervisorName(Course course) {
        Teacher supervisor = course.getSupervisor();
        return supervisor.getFirstName() + " " + supervisor.getLastName();
    }

    /**
     * Utility method to get the names of teachers of a course.
     * 
     * @param course The course
     * @return A set of teacher full names
     */
    private Set<String> GetTeacherNames(Course course) {
        return course.getTeachers()
                .stream()
                .map(teacher -> teacher.getFirstName() + " " + teacher.getLastName())
                .collect(Collectors.toSet());
    }
}
