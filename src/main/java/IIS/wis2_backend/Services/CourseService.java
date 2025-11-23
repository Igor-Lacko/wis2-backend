package IIS.wis2_backend.Services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import IIS.wis2_backend.DTO.Request.Course.CourseCreationDTO;
import IIS.wis2_backend.DTO.Request.Course.CourseDetailsUpdateDTO;
import IIS.wis2_backend.DTO.Request.ModelAttributes.CourseFilter;
import IIS.wis2_backend.DTO.Response.Course.CourseStatistics;
import IIS.wis2_backend.DTO.Response.Course.FullCourseDTO;
import IIS.wis2_backend.DTO.Response.Course.LightweightCourseDTO;
import IIS.wis2_backend.DTO.Response.Course.PendingRequestsListDTO;
import IIS.wis2_backend.DTO.Response.Course.RegisteredCourseListItemDTO;
import IIS.wis2_backend.DTO.Response.Course.SupervisorCourseDTO;
import IIS.wis2_backend.DTO.Response.Course.CourseShortened;
import IIS.wis2_backend.DTO.Response.Course.StudentGradeDTO;
import IIS.wis2_backend.DTO.Response.Course.GradebookEntryDTO;
import IIS.wis2_backend.DTO.Response.Course.TermGradeDTO;
import IIS.wis2_backend.DTO.Response.Course.TermListDTO;
import IIS.wis2_backend.DTO.Response.User.UserShortened;
import IIS.wis2_backend.DTO.Response.User.VerySmallUserDTO;
import IIS.wis2_backend.Models.Relational.StudentCourse;
import IIS.wis2_backend.Models.Relational.StudentTerm;
import IIS.wis2_backend.Models.Term.Term;
import IIS.wis2_backend.DTO.Response.NestedDTOs.TeacherDTOForCourse;
import IIS.wis2_backend.DTO.Response.Projections.LightweightCourseProjection;
import IIS.wis2_backend.Enum.CourseEndType;
import IIS.wis2_backend.Enum.CourseRoleType;
import IIS.wis2_backend.Enum.RequestStatus;
import IIS.wis2_backend.Exceptions.ExceptionTypes.AlreadySetException;
import IIS.wis2_backend.Exceptions.ExceptionTypes.InternalException;
import IIS.wis2_backend.Exceptions.ExceptionTypes.NotFoundException;
import IIS.wis2_backend.Exceptions.ExceptionTypes.UnauthorizedException;
import IIS.wis2_backend.Models.Course;
import IIS.wis2_backend.Models.Schedule;
import IIS.wis2_backend.Models.User.Wis2User;
import IIS.wis2_backend.Repositories.CourseRepository;
import IIS.wis2_backend.Repositories.User.UserRepository;
import IIS.wis2_backend.Repositories.Relational.StudentCourseRepository;
import IIS.wis2_backend.Repositories.Relational.StudentTermRepository;
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
	 * StudentTerm repository.
	 */
	private final StudentTermRepository studentTermRepository;

	/**
	 * StudentCourse repository.
	 */
	private final StudentCourseRepository studentCourseRepository;

	/**
	 * Constructor for CourseService.
	 * 
	 * @param courseRepository        the course repository
	 * @param userRepository          the user repository
	 * @param studentTermRepository   the student term repository
	 * @param studentCourseRepository the student course repository
	 */
	public CourseService(CourseRepository courseRepository, UserRepository userRepository,
			StudentTermRepository studentTermRepository, StudentCourseRepository studentCourseRepository) {
		this.courseRepository = courseRepository;
		this.userRepository = userRepository;
		this.studentTermRepository = studentTermRepository;
		this.studentCourseRepository = studentCourseRepository;
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

		List<LightweightCourseProjection> courses = courseRepository.findAllByStatus(RequestStatus.APPROVED);

		// Filter
		Set<LightweightCourseProjection> filteredCourses = courses.stream()
				.filter(c -> (filter.getQuery() == null
						|| c.getName().toLowerCase().contains(filter.getQuery().toLowerCase())
						|| c.getShortcut().toLowerCase()
								.contains(filter.getQuery().toLowerCase())))
				.filter(c -> (c.getPrice() >= minPrice && c.getPrice() <= maxPrice))
				.filter(c -> {
					if (filter.isEndedByBoth()) {
						return true;
					} else if (filter.isEndedByExam()) {
						return c.getCompletedBy().equals(CourseEndType.EXAM.name());
					} else if (filter.isEndedByGradedUnitCredit()) {
						return c.getCompletedBy()
								.equals(CourseEndType.GRADED_UNIT_CREDIT.name());
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
	public FullCourseDTO GetCourseByShortcut(String shortcut) {
		Course course = courseRepository.findByShortcut(shortcut)
				.orElseThrow(() -> new NotFoundException(
						"The course with this shortcut doesn't exist!"));
		return CourseToFullDTO(course);
	}

	/**
	 * Get all pending courses.
	 * 
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
	 * Returns the pending registrations for a course.
	 * 
	 * @param shortcut The course shortcut.
	 * @param username The username of the requester. Has to match the supervisor of
	 *                 the course.
	 * @return A DTO containing the pending registration requests.
	 */
	public PendingRequestsListDTO GetPendingRegistrations(String shortcut, String username) {
		if (!courseRepository.existsBySupervisor_UsernameAndShortcut(username, shortcut)) {
			throw new UnauthorizedException("User is not the supervisor of this course!");
		}

		List<VerySmallUserDTO> pendingRequests = studentCourseRepository
				.findByCourseShortcutAndStatus(shortcut, RequestStatus.PENDING).stream()
				.map(sc -> new VerySmallUserDTO(
						sc.getStudent().getId(),
						sc.getStudent().getUsername(),
						sc.getStudent().getFirstName(),
						sc.getStudent().getLastName()))
				.collect(Collectors.toList());

		long enrolledCount = courseRepository.getEnrolledCountByCourseShortcut(shortcut);

		return new PendingRequestsListDTO(enrolledCount, pendingRequests);
	}

	/**
	 * Create a new course.
	 * 
	 * @param courseCreationDTO  Course creation DTO.
	 * @param supervisorUsername Supervisor's username.
	 * 
	 * @return Created course as LightweightCourseDTO.
	 */
	public LightweightCourseDTO CreateCourse(CourseCreationDTO courseCreationDTO, String supervisorUsername) {
		// Find supervisor
		Wis2User supervisor = userRepository.findByUsername(supervisorUsername)
				.orElseThrow(() -> new NotFoundException("Supervisor not found"));

		if (courseRepository.existsByShortcut(courseCreationDTO.shortcut())) {
			throw new AlreadySetException(
					"Course with shortcut " + courseCreationDTO.shortcut()
							+ " already exists :(((");
		}

		Set<Wis2User> teachers = new HashSet<>();
		teachers.add(supervisor);

		// Create course
		Course course = Course.builder()
				.name(courseCreationDTO.name())
				.price(courseCreationDTO.price())
				.shortcut(courseCreationDTO.shortcut())
				.completedBy(courseCreationDTO.type())
				.capacity(courseCreationDTO.capacity())
				.autoregister(courseCreationDTO.autoregister())
				.supervisor(supervisor)
				.teachers(teachers)
				.status(RequestStatus.PENDING)
				.build();

		Schedule schedule = Schedule.builder()
				.course(course)
				.build();

		if (course == null) {
			throw new InternalException("Course creation failed!");
		}

		course.setSchedule(schedule);
		courseRepository.save(course);
		return CourseToLightweightDTO(course);
	}

	/**
	 * Approve a course.
	 * 
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
	 * 
	 * @param id Course ID.
	 */
	public void rejectCourse(Long id) {
		Course course = courseRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Course not found"));
		course.setStatus(IIS.wis2_backend.Enum.RequestStatus.REJECTED);
		courseRepository.save(course);
	}

	/**
	 * Retrieve courses by user role.
	 * 
	 * @param username The username of the user.
	 * @param role     The role of the user in the course.
	 * @return List of lightweight course DTOs.
	 */
	public List<LightweightCourseDTO> GetCoursesByRole(String username, CourseRoleType role) {
		switch (role) {
			case SUPERVISOR:
				return courseRepository
						.findBySupervisor_UsernameAndStatus(username, RequestStatus.APPROVED,
								LightweightCourseProjection.class)
						.stream()
						.map(this::LightweightProjectionToDTO)
						.collect(Collectors.toList());

			case TEACHER:
				return courseRepository
						.findByTeachers_UsernameAndStatus(username, RequestStatus.APPROVED,
								LightweightCourseProjection.class)
						.stream()
						.map(this::LightweightProjectionToDTO)
						.collect(Collectors.toList());

			case STUDENT:
				return courseRepository
						.findCoursesByStudentUsernameAndStatus(username,
								RequestStatus.APPROVED, RequestStatus.APPROVED,
								LightweightCourseProjection.class)
						.stream()
						.map(this::LightweightProjectionToDTO)
						.collect(Collectors.toList());
		}

		throw new IllegalArgumentException("Invalid role specified!");
	}

	/**
	 * Returns the SupervisorCourseDTO for a given course.
	 * 
	 * @param shortcut           The course shortcut.
	 * @param supervisorUsername The username of the supervisor requesting the view.
	 * @return The SupervisorCourseDTO.
	 */
	public SupervisorCourseDTO GetSupervisorView(String shortcut, String supervisorUsername) {
		Course course = courseRepository.findByShortcut(shortcut)
				.orElseThrow(() -> new NotFoundException(
						"The course with this shortcut doesn't exist!"));

		if (!course.getSupervisor().getUsername().equals(supervisorUsername)) {
			throw new UnauthorizedException("User is not the supervisor of this course!");
		}

		return new SupervisorCourseDTO(
				course.getName(),
				course.getPrice(),
				course.getDescription(),
				course.getShortcut(),
				course.getCompletedBy(),
				course.getCapacity(),
				course.getAutoregister());
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
		Wis2User supervisorProjection = userRepository
				.findTopBySupervisedCourses_Id(course.getId());
		List<Wis2User> teacherProjections = userRepository
				.findAllByTaughtCourses_Id(course.getId());

		// Map to DTOs
		TeacherDTOForCourse supervisor = supervisorProjection != null ? new TeacherDTOForCourse(
				supervisorProjection.getUsername(),
				supervisorProjection.getFirstName(),
				supervisorProjection.getLastName()) : null;

		Set<TeacherDTOForCourse> teachers = teacherProjections.stream()
				.map(t -> new TeacherDTOForCourse(t.getUsername(), t.getFirstName(), t.getLastName()))
				.collect(Collectors.toSet());

		return new FullCourseDTO(
				course.getId(),
				course.getName(),
				course.getPrice(),
				course.getDescription(),
				course.getShortcut(),
				supervisor,
				teachers,
				course.getCompletedBy().name());
	}

	/**
	 * Utility method to convert Course to LightweightCourseDTO.
	 * 
	 * @param course The course to convert
	 * @return The corresponding LightweightCourseDTO
	 */
	private LightweightCourseDTO CourseToLightweightDTO(Course course) {
		return new LightweightCourseDTO(
				course.getId(),
				course.getName(),
				course.getPrice(),
				course.getShortcut(),
				course.getCompletedBy().name());
	}

	/**
	 * Utility method to convert LightweightCourseProjection to
	 * LightweightCourseDTO.
	 * 
	 * @param projection The projection to convert
	 * @return The corresponding LightweightCourseDTO
	 */
	private LightweightCourseDTO LightweightProjectionToDTO(LightweightCourseProjection projection) {
		return new LightweightCourseDTO(
				projection.getId(),
				projection.getName(),
				projection.getPrice(),
				projection.getShortcut(),
				projection.getCompletedBy());
	}

	@Transactional
	public List<CourseShortened> getCoursesTaughtBy(String username) {
		Wis2User teacher = userRepository.findByUsername(username)
				.orElseThrow(() -> new NotFoundException("Teacher not found: " + username));

		return teacher.getTaughtCourses().stream()
				.map(course -> CourseShortened.builder()
						.id(course.getId())
						.name(course.getName())
						.price(course.getPrice())
						.shortcut(course.getShortcut())
						.completedBy(course.getCompletedBy().name())
						.build())
				.collect(Collectors.toList());
	}

	/**
	 * Approves a student's registration request for a course.
	 * 
	 * @param courseShortcut     The shortcut of the course.
	 * @param studentUsername    The username of the student whose request is to be
	 *                           approved.
	 * @param supervisorUsername The username of the supervisor approving the
	 *                           request.
	 */
	@Transactional
	public void ApproveRegistrationRequest(String courseShortcut, String studentUsername,
			String supervisorUsername) {
		Course course = courseRepository.findByShortcut(courseShortcut)
				.orElseThrow(() -> new NotFoundException("Course not found"));

		if (!course.getSupervisor().getUsername().equals(supervisorUsername)) {
			throw new UnauthorizedException("User is not the supervisor of this course!");
		}

		StudentCourse studentCourse = studentCourseRepository
				.findByCourseShortcutAndStatusAndStudentUsername(courseShortcut, RequestStatus.PENDING, studentUsername)
				.orElseThrow(() -> new NotFoundException("Registration request not found"));

		// Check if approving this student would exceed capacity
		long approvedCount = courseRepository.getEnrolledCountByCourseShortcut(courseShortcut);
		if (approvedCount >= course.getCapacity()) {
			throw new IllegalArgumentException(
					"Approving this student would exceed course capacity! Increase capacity first.");
		}

		studentCourse.setStatus(RequestStatus.APPROVED);
	}

	/**
	 * Rejects a student's registration request for a course.
	 * 
	 * @param courseShortcut     The shortcut of the course.
	 * @param studentUsername    The username of the student whose request is to be
	 *                           rejected.
	 * @param supervisorUsername The username of the supervisor rejecting the
	 *                           request.
	 */
	@Transactional
	public void RejectRegistrationRequest(String courseShortcut, String studentUsername,
			String supervisorUsername) {
		Course course = courseRepository.findByShortcut(courseShortcut)
				.orElseThrow(() -> new NotFoundException("Course not found"));

		if (!course.getSupervisor().getUsername().equals(supervisorUsername)) {
			throw new UnauthorizedException("User is not the supervisor of this course!");
		}

		StudentCourse studentCourse = studentCourseRepository
				.findByCourseShortcutAndStatusAndStudentUsername(courseShortcut, RequestStatus.PENDING, studentUsername)
				.orElseThrow(() -> new NotFoundException("Registration request not found"));

		studentCourse.setStatus(RequestStatus.REJECTED);
	}

	@Transactional
	public List<StudentGradeDTO> getStudentsInCourse(Long courseId) {
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new NotFoundException("Course not found: " + courseId));

		return course.getStudentCourses().stream()
				.filter(sc -> sc.getStatus() == RequestStatus.APPROVED)
				.map(sc -> StudentGradeDTO.builder()
						.student(UserShortened.builder()
								.id(sc.getStudent().getId())
								.username(sc.getStudent().getUsername())
								.firstName(sc.getStudent().getFirstName())
								.lastName(sc.getStudent().getLastName())
								.build())
						.grade(sc.getFinalGrade())
						.points(sc.getPoints())
						.unitCredit(sc.getUnitCredit())
						.examPassed(sc.getExamPassed())
						.completed(sc.getCompleted())
						.build())
				.collect(Collectors.toList());
	}

	@Transactional
	public void updateStudentGrade(Long courseId, Long studentId, Double grade) {
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new NotFoundException("Course not found: " + courseId));

		StudentCourse studentCourse = course.getStudentCourses().stream()
				.filter(sc -> sc.getStudent().getId().equals(studentId) && sc.getStatus() == RequestStatus.APPROVED)
				.findFirst()
				.orElseThrow(() -> new NotFoundException("Student not found in course"));

		studentCourse.setFinalGrade(grade);
		courseRepository.save(course);
	}

	/**
	 * Returns the terms of a specific course.
	 * 
	 * @param shortcut the course shortcut
	 * @param username the username of the user requesting the terms. has to be
	 *                 teacher or supervisor
	 * @return List of term DTOs for the course
	 */
	@Transactional
	public List<TermListDTO> GetCourseTerms(String shortcut, String username) {
		Course course = courseRepository.findByShortcutAndStatus(shortcut, RequestStatus.APPROVED)
				.orElseThrow(() -> new NotFoundException("Course not found"));

		if (!course.getSupervisor().getUsername().equals(username)
				&& !userRepository.existsByUsernameAndTaughtCourses_Shortcut(username, shortcut)
				&& !userRepository.existsByUsernameAndStudentCourses_Course_ShortcutAndStudentCourses_Status(username,
						shortcut, RequestStatus.APPROVED)) {
			throw new UnauthorizedException("User is not authorized to view terms of this course!");
		}

		Wis2User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new NotFoundException("User not found"));

		return course.getTerms().stream()
				.sorted((t1, t2) -> t1.getDate().compareTo(t2.getDate())) // Sort by date
				.map(term -> {
					boolean isRegistered = term.getStudentTerms().stream()
							.anyMatch(st -> st.getStudent().getId().equals(user.getId()));
					
					return new TermListDTO(
							term.getId(),
							term.getName(),
							term.getTermType(),
							term.getDuration(),
							term.getRoom().getShortcut(),
							term.getMinPoints(),
							term.getMaxPoints(),
							term.getDate(),
							isRegistered,
							term.getStudentTerms().size(),
							term.getRoom().getCapacity());
				})
				.collect(Collectors.toList());
	}

	/**
	 * Removes a teacher from a course.
	 * 
	 * @param courseShortcut     The course shortcut.
	 * @param teacherUsername    The username of the teacher to be removed.
	 * @param supervisorUsername The username of the supervisor performing the
	 *                           removal.
	 */
	@Transactional
	public void RemoveTeacherFromCourse(String courseShortcut, String teacherUsername, String supervisorUsername) {
		Course course = courseRepository.findByShortcut(courseShortcut)
				.orElseThrow(() -> new NotFoundException("Course not found"));

		if (!course.getSupervisor().getUsername().equals(supervisorUsername)) {
			throw new UnauthorizedException("User is not the supervisor of this course!");
		}

		Wis2User teacher = userRepository.findByUsername(teacherUsername)
				.orElseThrow(() -> new NotFoundException("Teacher not found"));

		if (!course.getTeachers().contains(teacher)) {
			throw new NotFoundException("Teacher is not assigned to this course!");
		}

		course.getTeachers().remove(teacher);

		// Remove all this-course related things from the teacher's schedule
		Schedule teacherSchedule = teacher.getSchedule();
		teacherSchedule.getItems().removeIf(item -> item.getCourseShortcut().equals(course.getShortcut()));
	}

	private String getTermType(Term term) {
		if (term instanceof IIS.wis2_backend.Models.Term.Exam)
			return "EXAM";
		if (term instanceof IIS.wis2_backend.Models.Term.MidtermExam)
			return "EXAM";
		if (term instanceof IIS.wis2_backend.Models.Term.Lab)
			return "LAB";
		return "ASSIGNMENT";
	}

	@Transactional
	public List<GradebookEntryDTO> getCourseGradebook(Long courseId) {
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new NotFoundException("Course not found"));

		return course.getStudentCourses().stream()
				.filter(sc -> sc.getStatus() == RequestStatus.APPROVED)
				.map(sc -> {
					Wis2User student = sc.getStudent();
					
					// Calculate term grades with enrollment status
					List<TermGradeDTO> termGrades = course.getTerms().stream()
							.map(term -> {
								// Find if student is enrolled in this term
								StudentTerm studentTerm = term.getStudentTerms().stream()
										.filter(st -> st.getStudent().getId().equals(student.getId()))
										.findFirst()
										.orElse(null);
								
								return TermGradeDTO.builder()
										.termId(term.getId())
										.termName(term.getName())
										.points(studentTerm != null ? studentTerm.getPoints() : null)
										.maxPoints(term.getMaxPoints())
										.enrolled(studentTerm != null)
										.build();
							})
							.collect(Collectors.toList());
					
					// Calculate total points from enrolled terms
					Integer totalPoints = termGrades.stream()
							.filter(tg -> tg.getEnrolled() && tg.getPoints() != null)
							.mapToInt(TermGradeDTO::getPoints)
							.sum();

					return GradebookEntryDTO.builder()
							.student(UserShortened.builder()
									.id(student.getId())
									.username(student.getUsername())
									.firstName(student.getFirstName())
									.lastName(student.getLastName())
									.build())
							.termGrades(termGrades)
							.totalPoints(totalPoints)
							.points(sc.getPoints())
							.unitCredit(sc.getUnitCredit())
							.examPassed(sc.getExamPassed())
							.finalGrade(sc.getFinalGrade())
							.completed(sc.getCompleted())
							.build();
				})
				.collect(Collectors.toList());
	}

	@Transactional
	public void updateStudentTermPoints(Long courseId, Long termId, Long studentId, Integer points) {
		if (!courseRepository.existsById(courseId)) {
			throw new NotFoundException("Course not found");
		}

		StudentTerm studentTerm = studentTermRepository.findByTermIdAndStudentId(termId, studentId)
				.orElseThrow(() -> new NotFoundException("StudentTerm not found"));

		if (!studentTerm.getTerm().getCourse().getId().equals(courseId)) {
			throw new IllegalArgumentException("Term does not belong to the specified course");
		}

		studentTerm.setPoints(points);
		studentTermRepository.save(studentTerm);
	}

	/**
	 * Updates the details of a specific course.
	 * 
	 * @param shortcut The shortcut of the course to be updated.
	 * @param dto      The DTO containing updated course details.
	 * @param username The username of the user making the update.
	 */
	public void UpdateCourseDetails(String shortcut, CourseDetailsUpdateDTO dto, String username) {
		Course course = courseRepository.findByShortcut(shortcut)
				.orElseThrow(() -> new NotFoundException(
						"The course with this shortcut doesn't exist!"));

		if (!course.getSupervisor().getUsername().equals(username)) {
			throw new UnauthorizedException("User is not the supervisor of this course!");
		}

		course.setPrice(dto.getPrice());
		course.setCapacity(dto.getCapacity());
		course.setAutoregister(dto.getAutoregister());

		if (dto.getDescription() != null) {
			course.setDescription(dto.getDescription());
		}

		courseRepository.save(course);
	}

	/**
	 * Adds a teacher to a course.
	 * 
	 * @param courseShortcut     The course shortcut.
	 * @param teacherUsername    The username of the teacher to be added.
	 * @param supervisorUsername The username of the supervisor performing the
	 *                           addition.
	 */
	@Transactional
	public void AddTeacherToCourse(String courseShortcut, String teacherUsername, String supervisorUsername) {
		Course course = courseRepository.findByShortcut(courseShortcut)
				.orElseThrow(() -> new NotFoundException("Course not found"));

		if (!course.getSupervisor().getUsername().equals(supervisorUsername)) {
			throw new UnauthorizedException("User is not the supervisor of this course!");
		}

		Wis2User teacher = userRepository.findByUsername(teacherUsername)
				.orElseThrow(() -> new NotFoundException("Teacher not found"));

		// I love BE comparing bruh
		if (course.getTeachers().stream()
				.anyMatch(t -> t.getUsername().equals(teacherUsername))) {
			throw new AlreadySetException("Teacher is already assigned to this course!");
		}

		course.getTeachers().add(teacher);

		// Add all course schedule items to the teacher's schedule
		Schedule teacherSchedule = teacher.getSchedule();
		course.getSchedule().getItems().forEach(item -> {
			teacherSchedule.getItems().add(item);
		});
	}

	/**
	 * Gets the list of teachers for a specific course.
	 * 
	 * @param shortcut The shortcut of the course.
	 * @return List of VerySmallUserDTO representing the teachers of the course.
	 */
	@Transactional
	public List<VerySmallUserDTO> GetCourseTeachers(String shortcut) {
		Course course = courseRepository.findByShortcut(shortcut)
				.orElseThrow(() -> new NotFoundException(
						"The course with this shortcut doesn't exist!"));

		return course.getTeachers().stream()
				.map(teacher -> new VerySmallUserDTO(
						teacher.getId(),
						teacher.getUsername(),
						teacher.getFirstName(),
						teacher.getLastName()))
				.collect(Collectors.toList());
	}

	/**
	 * Returns registered course DTO's depending on the user's role.
	 * 
	 * @param username The username of the user.
	 */
	@Transactional
	public List<RegisteredCourseListItemDTO> GetRegisteredCourseDTOs(String username) {
		if (!userRepository.existsByUsername(username)) {
			throw new NotFoundException("User not found: " + username);
		}

		return courseRepository.findAll()
				.stream()
				.map(c -> {
					String shortcut = c.getShortcut();
					boolean isSupervisor = courseRepository
							.existsBySupervisor_UsernameAndShortcut(username, shortcut);
					boolean isTeacher = courseRepository
							.existsByTeachers_UsernameAndShortcut(username, shortcut);
					boolean isStudent = courseRepository
							.existsByStudentCourses_Student_UsernameAndShortcut(username, shortcut);
					boolean hasRequested = userRepository
							.existsByUsernameAndStudentCourses_Course_ShortcutAndStudentCourses_Status(username,
									shortcut, RequestStatus.PENDING);

					return RegisteredCourseListItemDTO.builder()
							.id(c.getId())
							.name(c.getName())
							.price(c.getPrice())
							.shortcut(c.getShortcut())
							.completedBy(c.getCompletedBy())
							.isSupervisor(isSupervisor)
							.isTeacher(isTeacher)
							.isStudent(isStudent)
							.hasRequested(hasRequested)
							.build();
				})
				.collect(Collectors.toList());
	}

	/**
	 * Enrolls a student in a course.
	 * 
	 * @param courseShortcut  The shortcut of the course.
	 * @param studentUsername The username of the student.
	 * @return A message indicating the result of the enrollment.
	 */
	@Transactional
	public String EnrollInCourse(String courseShortcut, String studentUsername) {
		Course course = courseRepository.findByShortcut(courseShortcut)
				.orElseThrow(() -> new NotFoundException("Course not found"));

		// Check if student is already associated with the course
		if (courseRepository.existsBySupervisor_UsernameAndShortcut(studentUsername, courseShortcut)) {
			throw new AlreadySetException("User is the supervisor of this course!");
		} else if (courseRepository.existsByTeachers_UsernameAndShortcut(studentUsername, courseShortcut)) {
			throw new AlreadySetException("User is a teacher of this course!");
		} else if (courseRepository.existsByStudentCourses_Student_UsernameAndShortcut(studentUsername,
				courseShortcut)) {
			throw new AlreadySetException("User is already enrolled or has a pending request for this course!");
		}

		// Check capacity first
		long approvedCount = courseRepository.getEnrolledCountByCourseShortcut(courseShortcut);
		if (approvedCount >= course.getCapacity()) {
			throw new IllegalArgumentException(
					"Course capacity reached! Cannot enroll. Contact the course supervisor.");
		}

		// Autoregister
		RequestStatus status = course.getAutoregister() ? RequestStatus.APPROVED : RequestStatus.PENDING;
		studentCourseRepository.save(StudentCourse.builder()
				.course(course)
				.student(userRepository.findByUsername(studentUsername)
						.orElseThrow(() -> new NotFoundException("Student not found")))
				.status(status)
				.build());

		return status.equals(RequestStatus.APPROVED) ? "Enrolled successfully!"
				: "Enrollment request submitted successfully!";
	}

	/**
	 * Grants unit credit (zápočet) to a student in a course.
	 * 
	 * @param courseId        The ID of the course.
	 * @param studentId       The ID of the student.
	 * @param teacherUsername The username of the teacher granting the credit.
	 */
	@Transactional
	public void grantCredit(Long courseId, Long studentId, String teacherUsername) {
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new NotFoundException("Course not found: " + courseId));

		// Verify teacher authorization
		if (!course.getSupervisor().getUsername().equals(teacherUsername)
				&& !course.getTeachers().stream()
						.anyMatch(t -> t.getUsername().equals(teacherUsername))) {
			throw new UnauthorizedException("User is not authorized to grant credit in this course!");
		}

		// Find student-course relationship
		StudentCourse studentCourse = course.getStudentCourses().stream()
				.filter(sc -> sc.getStudent().getId().equals(studentId) && sc.getStatus() == RequestStatus.APPROVED)
				.findFirst()
				.orElseThrow(() -> new NotFoundException("Student not found in course"));

		// Check if credit already granted
		if (Boolean.TRUE.equals(studentCourse.getUnitCredit())) {
			throw new AlreadySetException("Unit credit already granted to this student!");
		}

		// Grant credit
		studentCourse.setUnitCredit(true);

		// If course is UNIT_CREDIT only, mark as completed
		if (course.getCompletedBy() == CourseEndType.UNIT_CREDIT) {
			studentCourse.setCompleted(true);
		}

		courseRepository.save(course);
	}

	/**
	 * Grants exam pass and calculates final grade for a student in a course.
	 * 
	 * @param courseId        The ID of the course.
	 * @param studentId       The ID of the student.
	 * @param teacherUsername The username of the teacher granting the exam.
	 */
	@Transactional
	public void grantExam(Long courseId, Long studentId, String teacherUsername) {
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new NotFoundException("Course not found: " + courseId));

		// Verify teacher authorization
		if (!course.getSupervisor().getUsername().equals(teacherUsername)
				&& !course.getTeachers().stream()
						.anyMatch(t -> t.getUsername().equals(teacherUsername))) {
			throw new UnauthorizedException("User is not authorized to grant exam in this course!");
		}

		// Find student-course relationship
		StudentCourse studentCourse = course.getStudentCourses().stream()
				.filter(sc -> sc.getStudent().getId().equals(studentId) && sc.getStatus() == RequestStatus.APPROVED)
				.findFirst()
				.orElseThrow(() -> new NotFoundException("Student not found in course"));

		// Check if exam already granted
		if (Boolean.TRUE.equals(studentCourse.getExamPassed())) {
			throw new AlreadySetException("Exam already granted to this student!");
		}

		// For UNIT_CREDIT_EXAM, student must have credit first
		if (course.getCompletedBy() == CourseEndType.UNIT_CREDIT_EXAM) {
			if (!Boolean.TRUE.equals(studentCourse.getUnitCredit())) {
				throw new IllegalArgumentException("Student must have unit credit before granting exam!");
			}
		}

		// Validate points
		Integer points = studentCourse.getPoints();
		if (points == null || points < 0 || points > 100) {
			throw new IllegalArgumentException("Invalid points! Points must be between 0 and 100.");
		}

		// Set exam as passed
		studentCourse.setExamPassed(true);

		// Calculate final grade (only if not UNIT_CREDIT)
		if (course.getCompletedBy() != CourseEndType.UNIT_CREDIT) {
			double finalGrade = calculateFinalGrade(points);
			studentCourse.setFinalGrade(finalGrade);
		}

		// Mark as completed
		studentCourse.setCompleted(true);

		courseRepository.save(course);
	}

	/**
	 * Calculates final grade based on points.
	 * 
	 * @param points The points earned (0-100).
	 * @return The final grade (1.0-4.0, or 5.0 for fail).
	 */
	private double calculateFinalGrade(int points) {
		if (points >= 90) {
			return 1.0;
		} else if (points >= 80) {
			return 1.5;
		} else if (points >= 70) {
			return 2.0;
		} else if (points >= 60) {
			return 2.5;
		} else if (points >= 50) {
			return 3.0;
		} else {
			return 4.0; // Fail
		}
	}
}
