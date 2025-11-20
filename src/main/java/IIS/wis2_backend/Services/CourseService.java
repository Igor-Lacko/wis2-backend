package IIS.wis2_backend.Services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import IIS.wis2_backend.DTO.Request.ModelAttributes.CourseFilter;
import IIS.wis2_backend.DTO.Response.Course.CourseStatistics;
import IIS.wis2_backend.DTO.Response.Course.FullCourseDTO;
import IIS.wis2_backend.DTO.Response.Course.LightweightCourseDTO;
import IIS.wis2_backend.DTO.Response.NestedDTOs.TeacherDTOForCourse;
import IIS.wis2_backend.DTO.Response.Projections.LightweightCourseProjection;
import IIS.wis2_backend.DTO.Response.Projections.TeacherForCourseProjection;
import IIS.wis2_backend.Enum.CourseEndType;
import IIS.wis2_backend.Exceptions.ExceptionTypes.NotFoundException;
import IIS.wis2_backend.Models.Course;
import IIS.wis2_backend.Repositories.CourseRepository;
import IIS.wis2_backend.Repositories.User.UserRepository;
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
     * Teacher repository to fetch supervisors/teachers.
     */
    private final UserRepository userRepository;

    /**
     * Constructor for CourseService.
     * 
     * @param courseRepository  the course repository
     * @param userRepository    the user repository
     */
    public CourseService(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
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
     * Get all pending courses.
     * @return List of pending courses.
     */
    public List<LightweightCourseDTO> getPendingCourses() {
        return courseRepository.findByStatus(IIS.wis2_backend.Enum.RequestStatus.PENDING).stream()
                .map(course -> new LightweightCourseDTO(
                        course.getId(),
                        course.getName(),
                        course.getPrice(),
                        course.getShortcut(),
                        course.getCompletedBy().toString()))
                .collect(Collectors.toList());
    }

    /**
     * Approve a course.
     * @param id Course ID.
     */
    public void approveCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course not found"));
        course.setStatus(IIS.wis2_backend.Enum.RequestStatus.APPROVED);
        courseRepository.save(course);
    }

    /**
     * Reject a course.
     * @param id Course ID.
     */
    public void rejectCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course not found"));
        course.setStatus(IIS.wis2_backend.Enum.RequestStatus.REJECTED);
        courseRepository.save(course);
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
        // Fetch supervisor and teacher projections
        TeacherForCourseProjection supervisorProjection = userRepository.findFirstBySupervisedCourses_Id(course.getId());
        List<TeacherForCourseProjection> teacherProjections = userRepository.findByTaughtCourses_Id(course.getId());

        // Map to DTOs
        TeacherDTOForCourse supervisor = new TeacherDTOForCourse(
                supervisorProjection.getId(),
                supervisorProjection.getFirstName(),
                supervisorProjection.getLastName());

        Set<TeacherDTOForCourse> teachers = teacherProjections.stream()
                .map(t -> new TeacherDTOForCourse(t.getId(), t.getFirstName(), t.getLastName()))
                .collect(Collectors.toSet());

        return new FullCourseDTO(
                course.getId(),
                course.getName(),
                course.getPrice(),
                course.getDescription(),
                course.getShortcut(),
                supervisor,
                teachers,
                course.getCompletedBy().name()
        );
    }
}
