package IIS.wis2_backend.Services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import IIS.wis2_backend.DTO.Course.FullCourseDTO;
import IIS.wis2_backend.DTO.Course.LightweightCourseDTO;
import IIS.wis2_backend.DTO.ModelAttributes.CourseFilter;
import IIS.wis2_backend.Enum.CourseEndType;
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
     * @param filter Course filter attributes.
     * @return a list of all courses
     */
    public List<LightweightCourseDTO> GetAllCourses(CourseFilter filter) {
        if (!IsValidSortByField(filter.getSortBy())) {
            throw new IllegalArgumentException("Invalid sortBy parameter!");
        }

        Specification<Course> spec = filter.getQuery() != null
            ? CourseSpecification.TitleOrShortcutContains(filter.getQuery())
            : null;

        // The individual course end types shown
        System.out.println("Filter ended by - both: " + filter.isEndedByBoth()
            + ", exam: " + filter.isEndedByExam()
            + ", unit credit: " + filter.isEndedByUnitCredit()
            + ", graded unit credit: " + filter.isEndedByGradedUnitCredit());
        Specification<Course> endedBySpec = null;
        if (filter.isEndedByBoth()) {
            endedBySpec = CourseSpecification.EndedBy(CourseEndType.UNIT_CREDIT_EXAM.name());
        }
        if (filter.isEndedByExam()) {
            endedBySpec = endedBySpec == null
                ? CourseSpecification.EndedBy(CourseEndType.EXAM.name())
                : endedBySpec.or(CourseSpecification.EndedBy(CourseEndType.EXAM.name()));
        }
        if (filter.isEndedByUnitCredit()) {
            endedBySpec = endedBySpec == null
                ? CourseSpecification.EndedBy(CourseEndType.UNIT_CREDIT.name())
                : endedBySpec.or(CourseSpecification.EndedBy(CourseEndType.UNIT_CREDIT.name()));
        }
        if (filter.isEndedByGradedUnitCredit()) {
            endedBySpec = endedBySpec == null
                ? CourseSpecification.EndedBy(CourseEndType.GRADED_UNIT_CREDIT.name())
                : endedBySpec.or(CourseSpecification.EndedBy(CourseEndType.GRADED_UNIT_CREDIT.name()));
        }

        if (endedBySpec != null) {
            spec = spec == null ? endedBySpec : spec.and(endedBySpec);
        }

        // Filter by price range
        if (filter.getMinPrice() != null || filter.getMaxPrice() != null) {
            Double min = filter.getMinPrice() != null ? filter.getMinPrice() : 0.0;
            Double max = filter.getMaxPrice() != null ? filter.getMaxPrice() : Double.MAX_VALUE;

            // Check for invalid args
            if (min < 0 || max < 0 || min > max) {
                throw new IllegalArgumentException("Invalid price range!");
            }

            spec = spec == null
                ? CourseSpecification.PriceIsInRange(min, max)
                : spec.and(CourseSpecification.PriceIsInRange(min, max));
        }

        Sort sort = CourseSpecification.BuildSort(filter.getSortBy(), filter.isReverse());
        List<Course> courses = courseRepository.findAll(
            spec,
            // Should never be null, but compiler warnings...
            sort == null ? Sort.by("name") : sort
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
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid course id!");
        }
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
     * Utility method to validate sortBy field.
     * 
     * @param sortBy The field to sort by
     * @return true if valid, false otherwise
     */
    private boolean IsValidSortByField(String sortBy) {
        return sortBy == null || sortBy.equals("name") || sortBy.equals("price");
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
