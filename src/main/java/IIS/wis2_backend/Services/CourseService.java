package IIS.wis2_backend.Services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import IIS.wis2_backend.DTO.Course.CourseStatistics;
import IIS.wis2_backend.DTO.Course.FullCourseDTO;
import IIS.wis2_backend.DTO.Course.LightweightCourseDTO;
import IIS.wis2_backend.DTO.ModelAttributes.CourseFilter;
import IIS.wis2_backend.DTO.Projections.LightweightCourseProjection;
import IIS.wis2_backend.Enum.CourseEndType;
import IIS.wis2_backend.Exceptions.ExceptionTypes.NotFoundException;
import IIS.wis2_backend.Models.Course;
import IIS.wis2_backend.Models.User.Teacher;
import IIS.wis2_backend.Repositories.CourseRepository;
import jakarta.transaction.Transactional;

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
    @Transactional
    public List<LightweightCourseDTO> GetAllCourses(CourseFilter filter) {
        if (!IsValidSortByField(filter.getSortBy())) {
            throw new IllegalArgumentException("Invalid sortBy parameter!");
        }

        double minPrice = filter.getMinPrice() != null ? filter.getMinPrice() : 0.0;
        double maxPrice = filter.getMaxPrice() != null ? filter.getMaxPrice() : Double.MAX_VALUE;

        if (minPrice < 0 || maxPrice < 0 || minPrice > maxPrice) {
            throw new IllegalArgumentException("Invalid price range!");
        }

        List<LightweightCourseProjection> courses = courseRepository.findAllBy();

        // TODO move filtering to database level?
        // Filter
        Set<LightweightCourseProjection> filteredCourses = courses.stream()
                .filter(c -> (filter.getQuery() == null
                        || c.getName().toLowerCase().contains(filter.getQuery().toLowerCase())
                        || c.getShortcut().toLowerCase().contains(filter.getQuery().toLowerCase())))
                .filter(c -> (c.getPrice() >= minPrice && c.getPrice() <= maxPrice))
                .filter(c -> {
                    if (filter.isEndedByBoth()) {
                        return true;
                    } else if (filter.isEndedByExam()) {
                        return c.getCompletedBy().equals(CourseEndType.EXAM.name());
                    } else if (filter.isEndedByGradedUnitCredit()) {
                        return c.getCompletedBy().equals(CourseEndType.GRADED_UNIT_CREDIT.name());
                    } else if (filter.isEndedByUnitCredit()) {
                        return c.getCompletedBy().equals(CourseEndType.UNIT_CREDIT.name());
                    } else {
                        return true;
                    }
                })
                .collect(Collectors.toSet());

        // Sort
        return filteredCourses.stream()
                // Convert to DTO
                .map(c -> new LightweightCourseDTO(
                        c.getId(),
                        c.getName(),
                        c.getPrice(),
                        c.getShortcut(),
                        c.getCompletedBy()))
                // Sort
                .sorted((c1, c2) -> {
                    // By price
                    if (filter.getSortBy().equals("price")) {
                        return filter.isReverse()
                                ? c2.getPrice().compareTo(c1.getPrice())
                                : c1.getPrice().compareTo(c2.getPrice());
                    }

                    // By name
                    else {
                        return filter.isReverse()
                                ? c2.getName().compareTo(c1.getName())
                                : c1.getName().compareTo(c2.getName());
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Getter for course price statistics (min and max price).
     * 
     * @return a DTO containing the min and max price
     */
    public CourseStatistics GetCoursePriceStatistics() {
        return new CourseStatistics(
                courseRepository.findMinPrice(),
                courseRepository.findMaxPrice());
    }

    /**
     * Getter for a course by id. Returns full DTO.
     * 
     * @param id the course id
     * @return the course with the given id
     * @throws IllegalArgumentException if the course with the given id does not
     *                                  exist
     */
    @Transactional
    public FullCourseDTO GetCourseById(long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("The course with this id doesn't exist!"));
        return CourseToFullDTO(course);
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
        // Fetch supervisor and teacher ids
        Long supervisorId = course.getSupervisor().getId();
        Set<Long> teacherIds = course.getTeachers()
                .stream()
                .map(Teacher::getId)
                .collect(Collectors.toSet());

        return new FullCourseDTO(
                course.getId(),
                course.getName(),
                course.getPrice(),
                course.getDescription(),
                course.getShortcut(),
                GetSupervisorName(course),
                supervisorId,
                teacherIds,
                GetTeacherNames(course),
                course.getCompletedBy().name());
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
