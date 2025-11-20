package IIS.wis2_backend.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import IIS.wis2_backend.DTO.Request.Course.CourseCreationDTO;
import IIS.wis2_backend.DTO.Request.ModelAttributes.CourseFilter;
import IIS.wis2_backend.DTO.Response.Course.CourseStatistics;
import IIS.wis2_backend.DTO.Response.Course.FullCourseDTO;
import IIS.wis2_backend.DTO.Response.Course.LightweightCourseDTO;
import IIS.wis2_backend.DTO.Response.Course.CourseShortened;
import IIS.wis2_backend.DTO.Response.Course.StudentGradeDTO;
import IIS.wis2_backend.DTO.Response.Course.TermListDTO;
import IIS.wis2_backend.DTO.Response.Course.GradebookEntryDTO;
import IIS.wis2_backend.DTO.Request.Course.TermPointsUpdateDTO;
import IIS.wis2_backend.DTO.Request.Course.GradeUpdateDTO;
import IIS.wis2_backend.Enum.CourseRoleType;
import IIS.wis2_backend.Services.CourseService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;

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
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * Getter for all courses.
     * 
     * @param filter Course filter attributes.
     * @return list of all courses matching the criteria.
     */
    @GetMapping
    public List<LightweightCourseDTO> GetAllCourses(@ModelAttribute CourseFilter filter) {
        return courseService.GetAllCourses(filter);
    }

    /**
     * Returns the min and max price of the currently present courses.
     * 
     * @return a DTO containing the min and max price
     */
    @GetMapping("/statistics")
    public CourseStatistics GetCoursePriceStatistics() {
        return courseService.GetCoursePriceStatistics();
    }

    /**
     * Getter for a course by id.
     * 
     * @param id Course id.
     */
    @GetMapping("/{shortcut}")
    public FullCourseDTO GetCourseByShortcut(@PathVariable String shortcut) {
        return courseService.GetCourseByShortcut(shortcut);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public List<LightweightCourseDTO> getPendingCourses() {
        return courseService.getPendingCourses();
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public void approveCourse(@PathVariable Long id) {
        courseService.approveCourse(id);
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public void rejectCourse(@PathVariable Long id) {
        courseService.rejectCourse(id);
    }

    /**
     * Creates a new course.
     * 
     * @param dto         The course creation DTO.
     * @param userDetails The authenticated user details for supervisor assignment.
     * @return The created course as a lightweight DTO.
     */
    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LightweightCourseDTO> createCourse(@RequestBody @Valid CourseCreationDTO dto,
            Authentication authentication) {
        LightweightCourseDTO createdCourse = courseService.CreateCourse(dto, authentication.getName());
        return ResponseEntity.ok(createdCourse);
    }

    /**
     * Get courses supervised by a specific user.
     * 
     * @param username The username of the supervisor.
     * @return List of lightweight course DTOs.
     */
    @GetMapping("/supervised-by/{username}")
    @PreAuthorize("#username == authentication.name")
    public ResponseEntity<List<LightweightCourseDTO>> GetCoursesBySupervisor(@PathVariable String username) {
        List<LightweightCourseDTO> courses = courseService.GetCoursesByRole(username, CourseRoleType.SUPERVISOR);
        return ResponseEntity.ok(courses);
    }

    /**
     * Get courses taught by a specific teacher.
     * 
     * @param username The username of the teacher.
     * @return List of shortened course DTOs.
     */
    @GetMapping("/taught-by/{username}")
    public ResponseEntity<List<CourseShortened>> getCoursesTaughtBy(@PathVariable String username) {
        return ResponseEntity.ok(courseService.getCoursesTaughtBy(username));
    }

    /**
     * Get courses studied by a specific student.
     * 
     * @param username The username of the student.
     * @return List of lightweight course DTOs.
     */
    @GetMapping("/studied-by/{username}")
    @PreAuthorize("#username == authentication.name")
    public ResponseEntity<List<LightweightCourseDTO>> GetCoursesByStudent(@PathVariable String username) {
        List<LightweightCourseDTO> courses = courseService.GetCoursesByRole(username, CourseRoleType.STUDENT);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{courseId}/students")
    public ResponseEntity<List<StudentGradeDTO>> getStudentsInCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getStudentsInCourse(courseId));
    }

    @PostMapping("/{courseId}/students/{studentId}/grade")
    public ResponseEntity<Void> updateStudentGrade(@PathVariable Long courseId, @PathVariable Long studentId, @RequestBody GradeUpdateDTO gradeDTO) {
        courseService.updateStudentGrade(courseId, studentId, gradeDTO.getGrade());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{courseId}/terms")
    public ResponseEntity<List<TermListDTO>> getCourseTerms(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getCourseTerms(courseId));
    }

    @GetMapping("/{courseId}/gradebook")
    public ResponseEntity<List<GradebookEntryDTO>> getCourseGradebook(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getCourseGradebook(courseId));
    }

    @PostMapping("/{courseId}/terms/{termId}/students/{studentId}/grade")
    public ResponseEntity<Void> updateStudentTermPoints(
            @PathVariable Long courseId,
            @PathVariable Long termId,
            @PathVariable Long studentId,
            @RequestBody TermPointsUpdateDTO dto) {
        courseService.updateStudentTermPoints(courseId, termId, studentId, dto.getPoints());
        return ResponseEntity.ok().build();
    }
}