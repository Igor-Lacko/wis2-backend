package IIS.wis2_backend.Services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import IIS.wis2_backend.DTO.Course.FullCourseDTO;
import IIS.wis2_backend.DTO.Course.LightweightCourseDTO;
import IIS.wis2_backend.Exceptions.ExceptionTypes.NotFoundException;
import IIS.wis2_backend.Models.Course;
import IIS.wis2_backend.Models.User.Teacher;
import IIS.wis2_backend.Repositories.CourseRepository;

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
     * @return a list of all courses
     */
    public List<LightweightCourseDTO> GetAllCourses() {
        return courseRepository.findAll().stream()
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
