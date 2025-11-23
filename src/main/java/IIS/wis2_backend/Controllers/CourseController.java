package IIS.wis2_backend.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import IIS.wis2_backend.DTO.Request.Course.CourseCreationDTO;
import IIS.wis2_backend.DTO.Request.Course.CourseDetailsUpdateDTO;
import IIS.wis2_backend.DTO.Request.ModelAttributes.CourseFilter;
import IIS.wis2_backend.DTO.Request.Term.TermCreationDTO;
import IIS.wis2_backend.DTO.Response.Course.CourseStatistics;
import IIS.wis2_backend.DTO.Response.Course.FullCourseDTO;
import IIS.wis2_backend.DTO.Response.Course.LightweightCourseDTO;
import IIS.wis2_backend.DTO.Response.Course.PendingRequestsListDTO;
import IIS.wis2_backend.DTO.Response.Course.RegisteredCourseListItemDTO;
import IIS.wis2_backend.DTO.Response.Course.SupervisorCourseDTO;
import IIS.wis2_backend.DTO.Response.Course.CourseShortened;
import IIS.wis2_backend.DTO.Response.Course.StudentGradeDTO;
import IIS.wis2_backend.DTO.Response.Course.TermListDTO;
import IIS.wis2_backend.DTO.Response.User.VerySmallUserDTO;
import IIS.wis2_backend.DTO.Response.Course.GradebookEntryDTO;
import IIS.wis2_backend.DTO.Request.Course.TermPointsUpdateDTO;
import IIS.wis2_backend.DTO.Request.Course.GradeUpdateDTO;
import IIS.wis2_backend.Enum.CourseRoleType;
import IIS.wis2_backend.Enum.TermType;
import IIS.wis2_backend.Services.CourseService;
import IIS.wis2_backend.Services.Education.TermService;
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
	 * Term service.
	 */
	private final TermService termService;

	/**
	 * Constructor for CourseController.
	 * 
	 * @param courseService Course service.
	 * @param termService   Term service.
	 */
	public CourseController(CourseService courseService, TermService termService) {
		this.courseService = courseService;
		this.termService = termService;
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

	/**
	 * Returns the supervisor view/DTO for a course.
	 * 
	 * @param shortcut The course shortcut.
	 * @return The supervisor DTO for the course.
	 */
	@GetMapping("/{shortcut}/supervisor-details")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<SupervisorCourseDTO> GetSupervisorCourseView(@PathVariable String shortcut,
			Authentication authentication) {
		SupervisorCourseDTO courseDTO = courseService.GetSupervisorView(shortcut, authentication.getName());
		return ResponseEntity.ok(courseDTO);
	}

	/**
	 * Returns the terms for a specific course.
	 * 
	 * @param shortcut The course shortcut.
	 * @return List of lightweight term DTOs.
	 */
	@GetMapping("/{shortcut}/terms")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<TermListDTO>> GetCourseTerms(
			@PathVariable String shortcut,
			Authentication authentication) {
		List<TermListDTO> terms = courseService.GetCourseTerms(shortcut, authentication.getName());
		return ResponseEntity.ok(terms);
	}

	@GetMapping("/{shortcut}/students")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Void> GetCourseStudents(@PathVariable String shortcut) {
		// Implementation for fetching course students goes here
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{courseId}/students")
	public ResponseEntity<List<StudentGradeDTO>> getStudentsInCourse(@PathVariable Long courseId) {
		return ResponseEntity.ok(courseService.getStudentsInCourse(courseId));
	}

	@PostMapping("/{courseId}/students/{studentId}/grade")
	public ResponseEntity<Void> updateStudentGrade(@PathVariable Long courseId, @PathVariable Long studentId,
			@RequestBody GradeUpdateDTO gradeDTO) {
		courseService.updateStudentGrade(courseId, studentId, gradeDTO.getGrade());
		return ResponseEntity.ok().build();
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

	/**
	 * Updates the details of a specific course.
	 * 
	 * @param shortcut       The shortcut of the course to be updated.
	 * @param dto            The DTO containing updated course details.
	 * @param authentication The authentication object of the current user.
	 * @return A ResponseEntity indicating the result of the operation.
	 */
	@PatchMapping("/{shortcut}/details")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Void> UpdateCourseDetails(
			@PathVariable String shortcut,
			@RequestBody @Valid CourseDetailsUpdateDTO dto,
			Authentication authentication) {
		courseService.UpdateCourseDetails(shortcut, dto, authentication.getName());
		return ResponseEntity.ok().build();
	}

	/**
	 * Returns all pending registrations for a course.
	 * 
	 * @param shortcut       The course shortcut.
	 * @param authentication The authentication object of the current user.
	 * @return A DTO containing the pending registration requests.
	 */
	@GetMapping("/{shortcut}/requests")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<PendingRequestsListDTO> GetPendingRegistrations(@PathVariable String shortcut,
			Authentication authentication) {
		PendingRequestsListDTO pendingRequests = courseService.GetPendingRegistrations(shortcut,
				authentication.getName());
		return ResponseEntity.ok(pendingRequests);
	}

	/**
	 * Approves a student's registration request for a course.
	 * 
	 * @param shortcut       The course shortcut.
	 * @param username       The username of the student whose request is to be
	 *                       approved.
	 * @param authentication The authentication object of the current user.
	 * @return A ResponseEntity indicating the result of the operation.
	 */
	@PostMapping("/{shortcut}/requests/{username}/approve")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Void> ApproveRegistrationRequest(
			@PathVariable String shortcut,
			@PathVariable String username,
			Authentication authentication) {
		courseService.ApproveRegistrationRequest(shortcut, username, authentication.getName());
		return ResponseEntity.ok().build();
	}

	/**
	 * Rejects a student's registration request for a course.
	 * 
	 * @param shortcut       The course shortcut.
	 * @param username       The username of the student whose request is to be
	 *                       rejected.
	 * @param authentication The authentication object of the current user.
	 * @return A ResponseEntity indicating the result of the operation.
	 */
	@PostMapping("/{shortcut}/requests/{username}/reject")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Void> RejectRegistrationRequest(
			@PathVariable String shortcut,
			@PathVariable String username,
			Authentication authentication) {
		courseService.RejectRegistrationRequest(shortcut, username, authentication.getName());
		return ResponseEntity.ok().build();
	}

	/**
	 * Removes a teacher from a course.
	 * 
	 * @param shortcut       The course shortcut.
	 * @param username       The username of the teacher to be removed.
	 * @param authentication The authentication object of the current user.
	 * @return A ResponseEntity indicating the result of the operation.
	 */
	@DeleteMapping("/{shortcut}/teachers/{username}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Void> RemoveTeacherFromCourse(
			@PathVariable String shortcut,
			@PathVariable String username,
			Authentication authentication) {
		courseService.RemoveTeacherFromCourse(shortcut, username, authentication.getName());
		return ResponseEntity.ok().build();
	}

	/**
	 * Adds a teacher to a course.
	 * 
	 * @param shortcut       The course shortcut.
	 * @param username       The username of the teacher to be added.
	 * @param authentication The authentication object of the current user.
	 */
	@PostMapping("/{shortcut}/teachers/{username}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Void> AddTeacherToCourse(
			@PathVariable String shortcut,
			@PathVariable String username,
			Authentication authentication) {
		courseService.AddTeacherToCourse(shortcut, username, authentication.getName());
		return ResponseEntity.ok().build();
	}

	/**
	 * Gets all teachers of a course.
	 * 
	 * @param shortcut The course shortcut.
	 * @return List of very small user DTOs representing the teachers.
	 */
	@GetMapping("/{shortcut}/teachers")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<VerySmallUserDTO>> GetCourseTeachers(@PathVariable String shortcut) {
		List<VerySmallUserDTO> teachers = courseService.GetCourseTeachers(shortcut);
		return ResponseEntity.ok(teachers);
	}

	/**
	 * Gets courses not associated with a specific user.
	 * 
	 * @param username The username of the user.
	 * @return List of lightweight course DTOs.
	 */
	@GetMapping("/registered-view")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<RegisteredCourseListItemDTO>> GetRegisteredCourseDTOs(Authentication authentication) {
		List<RegisteredCourseListItemDTO> courses = courseService.GetRegisteredCourseDTOs(authentication.getName());
		return ResponseEntity.ok(courses);
	}

	/**
	 * Tries to enroll the authenticated user in the specified course.
	 * 
	 * @param shortcut       The course shortcut.
	 * @param authentication The authentication object of the current user.
	 * @return A ResponseEntity indicating the result of the operation.
	 */
	@PostMapping("/{shortcut}/enroll")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<String> EnrollInCourse(@PathVariable String shortcut, Authentication authentication) {
		String result = courseService.EnrollInCourse(shortcut, authentication.getName());
		return ResponseEntity.ok(result);
	}

	/**
	 * Creates a new term.
	 * 
	 * @param shortcut       The course shortcut.
	 * @param dto            The term creation DTO.
	 * @param authentication The authentication object of the current user.
	 * @return A ResponseEntity indicating the result of the operation.
	 */
	@PostMapping("/{shortcut}/terms")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Void> CreateTerm(
			@PathVariable String shortcut,
			@RequestBody @Valid TermCreationDTO dto,
			Authentication authentication) {
		String supervisorUsername = authentication.getName();

		if (dto.getType() == TermType.EXAM) {
			termService.CreateFinalExam(shortcut, dto, supervisorUsername);
		} else {
			termService.CreateNonExamTerm(shortcut, dto, supervisorUsername);
		}

		return ResponseEntity.ok().build();
	}

	/**
	 * Registers the authenticated student to a term.
	 * 
	 * @param termId         The ID of the term.
	 * @param authentication The authentication object of the current user.
	 * @return A ResponseEntity indicating the result of the operation.
	 */
	@PostMapping("/terms/{termId}/register")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Void> RegisterForTerm(
			@PathVariable Long termId,
			Authentication authentication) {
		termService.RegisterStudentToTerm(termId, authentication.getName());
		return ResponseEntity.ok().build();
	}

	/**
	 * Unregisters the authenticated student from a term.
	 * 
	 * @param termId         The ID of the term.
	 * @param authentication The authentication object of the current user.
	 * @return A ResponseEntity indicating the result of the operation.
	 */
	@DeleteMapping("/terms/{termId}/register")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Void> UnregisterFromTerm(
			@PathVariable Long termId,
			Authentication authentication) {
		termService.UnregisterStudentFromTerm(termId, authentication.getName());
		return ResponseEntity.ok().build();
	}
}