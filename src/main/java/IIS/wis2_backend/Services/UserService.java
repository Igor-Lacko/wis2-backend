package IIS.wis2_backend.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import IIS.wis2_backend.DTO.Request.Auth.RegisterDTO;
import IIS.wis2_backend.DTO.Request.Room.OfficeShortcutDTO;
import IIS.wis2_backend.DTO.Request.User.UpdateUserRequest;
import IIS.wis2_backend.DTO.Response.Course.UserCoursesDTO;
import IIS.wis2_backend.DTO.Response.NestedDTOs.CourseDTOForTeacher;
import IIS.wis2_backend.DTO.Response.NestedDTOs.OfficeDTOForTeacher;
import IIS.wis2_backend.DTO.Response.NestedDTOs.OverviewCourseDTO;
import IIS.wis2_backend.DTO.Response.Projections.OverviewCourseProjection;
import IIS.wis2_backend.DTO.Response.User.AdminUserDTO;
import IIS.wis2_backend.DTO.Response.User.PendingRequestDTO;
import IIS.wis2_backend.DTO.Response.User.TeacherDTO;
import IIS.wis2_backend.DTO.Response.User.UserDTO;
import IIS.wis2_backend.DTO.Response.User.VerySmallUserDTO;
import IIS.wis2_backend.Enum.Roles;
import IIS.wis2_backend.Enum.PendingRequestType;
import IIS.wis2_backend.Enum.RequestStatus;
import IIS.wis2_backend.Exceptions.ExceptionTypes.InternalException;
import IIS.wis2_backend.Exceptions.ExceptionTypes.NotFoundException;
import IIS.wis2_backend.Exceptions.ExceptionTypes.UserAlreadyExistsException;
import IIS.wis2_backend.Models.Schedule;
import IIS.wis2_backend.Models.User.*;
import IIS.wis2_backend.Repositories.CourseRepository;
import IIS.wis2_backend.Repositories.Room.RoomRequestRepository;
import IIS.wis2_backend.Repositories.User.UserRepository;

/**
 * Service for user (including teachers, students) related operations.
 */
@Service
public class UserService {
	/**
	 * Repo for all users. Used when listing by name, etc.
	 */
	private final UserRepository userRepository;

	/**
	 * Course repository, to get courses taught/supervised by a teacher.
	 */
	private final CourseRepository courseRepository;

	/**
	 * Room request repository for getting pending room requests.
	 */
	private final RoomRequestRepository roomRequestRepository;

	/**
	 * Constructor for UserService.
	 * 
	 * @param userRepository   User repository.
	 * @param courseRepository Course repository.
	 */
	public UserService(UserRepository userRepository, CourseRepository courseRepository,
			RoomRequestRepository roomRequestRepository) {
		this.userRepository = userRepository;
		this.courseRepository = courseRepository;
		this.roomRequestRepository = roomRequestRepository;
	}

	/**
	 * Returns a public profile of a teacher by their username.
	 * 
	 * @param username The username of the user (teacher).
	 * @return The public profile of the teacher.
	 */
	public TeacherDTO GetTeacherPublicProfile(String username) {
		Wis2User teacher = userRepository.findByUsername(username).orElseThrow(
				() -> new NotFoundException("Teacher with this username does not exist!"));

		// Check if the user is a teacher
		if (teacher.getTaughtCourses().isEmpty() && teacher.getSupervisedCourses().isEmpty()) {
			throw new NotFoundException("User with this username is not a teacher!");
		}

		return TeacherToDTO(teacher);
	}

	/**
	 * Create a new user during registration.
	 * 
	 * @param registerDTO DTO with registration data.
	 * @return Created UserDTO.
	 */
	public UserDTO CreateUser(RegisterDTO registerDTO) {
		String firstName = registerDTO.getFirstName();
		String lastName = registerDTO.getLastName();

		// Generate a username based on first and last name
		String baseUsername = (firstName.substring(0, Math.min(firstName.length(), 4)).toLowerCase() +
				lastName.substring(0, Math.min(lastName.length(), 4)).toLowerCase());
		String username = baseUsername;
		int suffix = 2;

		// Add random digits until a unique username is found
		while (userRepository.existsByUsername(username)) {
			username = baseUsername + suffix;
			suffix++;
		}

		// Create user
		Wis2User user = Wis2User.builder()
				.firstName(registerDTO.getFirstName())
				.lastName(registerDTO.getLastName())
				.email(registerDTO.getEmail())
				.username(username)
				.birthday(registerDTO.getBirthday())
				.password(registerDTO.getPassword())
				.role(Roles.USER)
				.activated(false)
				.build();

		if (user == null) {
			throw new InternalException("User could not be created!");
		}

		Schedule schedule = Schedule.builder()
				.user(user)
				.build();

		user.setSchedule(schedule);

		return UserToDTO(userRepository.save(user));
	}

	/**
	 * Returns all courses the user is associated with.
	 * 
	 * @param username Username of the user.
	 * @return UserCoursesDTO containing sets of supervised, taught and enrolled
	 *         courses.
	 */
	public UserCoursesDTO GetUserCourses(String username) {
		return UserCoursesDTO.builder()
				.supervisedCourses(courseRepository
						.findBySupervisor_UsernameAndStatus(username, RequestStatus.APPROVED,
								OverviewCourseProjection.class)
						.stream()
						.map(this::OverviewProjectionToDTO)
						.collect(Collectors.toList()))
				.teachingCourses(courseRepository
						.findByTeachers_UsernameAndStatus(username, RequestStatus.APPROVED,
								OverviewCourseProjection.class)
						.stream()
						.map(this::OverviewProjectionToDTO)
						.collect(Collectors.toList()))
				.enrolledCourses(courseRepository
						.findCoursesByStudentUsernameAndStatus(username, RequestStatus.APPROVED,
								RequestStatus.APPROVED,
								OverviewCourseProjection.class)
						.stream()
						.map(this::OverviewProjectionToDTO)
						.collect(Collectors.toList()))
				.build();
	}

	/**
	 * For AuthController. Returns user role and id to send back to the client.
	 * 
	 * @param username the username of the user.
	 * @return pair of user id and role.
	 */
	public Pair<Long, String> GetUserIdAndRole(String username) {
		Wis2User user = userRepository.findByUsername(username)
				// Shouldn't happen since it's after authentication
				.orElseThrow(() -> new NotFoundException("This user doesn't exist!"));
		return Pair.of(user.getId(), user.getRole().name());
	}

	/**
	 * Convert User entity to UserDTO.
	 * 
	 * @param user User entity.
	 * @return UserDTO.
	 */
	private UserDTO UserToDTO(Wis2User user) {
		return UserDTO.builder()
				.id(user.getId())
				.username(user.getUsername())
				.firstName(user.getFirstName())
				.lastName(user.getLastName())
				.email(user.getEmail())
				.birthday(user.getBirthday())
				.telephoneNumber(user.getTelephoneNumber())
				.build();
	}

	/**
	 * Convert Teacher entity to TeacherDTO.
	 * 
	 * @param teacher Teacher entity.
	 * @return TeacherDTO.
	 */
	private TeacherDTO TeacherToDTO(Wis2User teacher) {
		// Fetch supervised courses
		Set<CourseDTOForTeacher> supervisedCourses = courseRepository
				.findBySupervisor_IdAndStatus(teacher.getId(), RequestStatus.APPROVED)
				.stream()
				.map(proj -> new CourseDTOForTeacher(
						proj.getId(),
						proj.getName(),
						proj.getShortcut()))
				.collect(Collectors.toSet());

		// And taught courses!
		Set<CourseDTOForTeacher> taughtCourses = courseRepository
				.findByTeachers_IdAndStatus(teacher.getId(), RequestStatus.APPROVED)
				.stream()
				.map(proj -> new CourseDTOForTeacher(
						proj.getId(),
						proj.getName(),
						proj.getShortcut()))
				.collect(Collectors.toSet());

		// Also map office
		OfficeDTOForTeacher officeDTO = null;
		if (teacher.getOffice() != null) {
			officeDTO = new OfficeDTOForTeacher(
					teacher.getOffice().getId(),
					teacher.getOffice().getShortcut());
		}

		return TeacherDTO.builder()
				.id(teacher.getId())
				.firstName(teacher.getFirstName())
				.lastName(teacher.getLastName())
				.username(teacher.getUsername())
				.email(teacher.getEmail())
				.office(officeDTO)
				.supervisedCourses(supervisedCourses)
				.taughtCourses(taughtCourses)
				.build();
	}

	/**
	 * Convert OverviewCourseProjection to OverviewCourseDTO.
	 * 
	 * @param proj OverviewCourseProjection.
	 * @return OverviewCourseDTO.
	 */
	private OverviewCourseDTO OverviewProjectionToDTO(OverviewCourseProjection proj) {
		return new OverviewCourseDTO(
				proj.getName(),
				proj.getShortcut());
	}

	public UserDTO GetUserById(long userId) {
		Wis2User user = userRepository.findById(userId).orElseThrow(
				() -> new NotFoundException("User with this ID does not exist!"));

		return UserToDTO(user);
	}

	/**
	 * Get all users.
	 * 
	 * @return List of all users.
	 */
	public java.util.List<AdminUserDTO> getAllUsers() {
		return userRepository.findAll().stream()
				.map(user -> AdminUserDTO.builder()
						.id(user.getId())
						.username(user.getUsername())
						.firstName(user.getFirstName())
						.lastName(user.getLastName())
						.email(user.getEmail())
						.birthday(user.getBirthday())
						.telephoneNumber(user.getTelephoneNumber())
						.activated(user.isActivated())
						.build())
				.collect(Collectors.toList());
	}

	/**
	 * Returns all teachers.
	 */
	public List<VerySmallUserDTO> getAllTeachers() {
		return userRepository.findAllByRole(Roles.TEACHER).stream()
				.map(this::toVerySmallUserDTO)
				.collect(Collectors.toList());
	}

	/**
	 * Returns all teachers who do not have an assigned office.
	 */
	public List<VerySmallUserDTO> getTeachersWithoutOffice() {
		return userRepository.findAllByRoleAndOfficeIsNull(Roles.TEACHER).stream()
				.map(this::toVerySmallUserDTO)
				.collect(Collectors.toList());
	}

	/**
	 * Delete a user by ID.
	 * 
	 * @param id User ID.
	 */
	public void deleteUser(Long id) {
		Wis2User user = userRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("User not found"));

		// Remove from supervised courses
		var supervised = new ArrayList<>(user.getSupervisedCourses());
		if (!supervised.isEmpty()) {
			for (var course : supervised) {
				course.setSupervisor(null);
				courseRepository.save(course);
			}
		}

		// Remove from taught courses
		var taught = new ArrayList<>(user.getTaughtCourses());
		if (!taught.isEmpty()) {
			for (var course : taught) {
				course.getTeachers().remove(user);
				courseRepository.save(course);
			}
		}

		userRepository.delete(user);
	}

	/**
	 * Promote a user to ADMIN.
	 * 
	 * @param id User ID.
	 */
	public void promoteUser(Long id) {
		Wis2User user = userRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("User not found"));
		user.setRole(Roles.ADMIN);
		userRepository.save(user);
	}

	/**
	 * Update user profile.
	 * 
	 * @param id      User ID.
	 * @param request Update request.
	 * @return Updated UserDTO.
	 */
	public UserDTO updateUser(Long id, UpdateUserRequest request) {
		Wis2User user = userRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("User not found"));

		if (request.getFirstName() != null) {
			user.setFirstName(request.getFirstName());
		}
		if (request.getLastName() != null) {
			user.setLastName(request.getLastName());
		}
		if (request.getEmail() != null) {
			if (!user.getEmail().equals(request.getEmail())
					&& userRepository.existsByEmail(request.getEmail())) {
				throw new UserAlreadyExistsException("Email already in use");
			}
			user.setEmail(request.getEmail());
		}
		if (request.getBirthday() != null) {
			user.setBirthday(request.getBirthday());
		}
		if (request.getTelephoneNumber() != null) {
			if (!request.getTelephoneNumber().equals(user.getTelephoneNumber())
					&& userRepository.existsByTelephoneNumber(request.getTelephoneNumber())) {
				throw new UserAlreadyExistsException("Telephone number already in use");
			}
			user.setTelephoneNumber(request.getTelephoneNumber());
		}

		return UserToDTO(userRepository.save(user));
	}

	/**
	 * Returns all users whose first or last name contains the given string (case
	 * insensitive).
	 * 
	 * @param namePart The string to search for in first or last names.
	 * @return List of UserDTOs matching the search criteria.
	 */
	public List<VerySmallUserDTO> GetUsersByNamePart(String namePart) {
		return userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(namePart, namePart)
				.stream()
				.map(this::toVerySmallUserDTO)
				.collect(Collectors.toList());
	}

	private VerySmallUserDTO toVerySmallUserDTO(Wis2User user) {
		return new VerySmallUserDTO(
				user.getId(),
				user.getUsername(),
				user.getFirstName(),
				user.getLastName());
	}

	/**
	 * Returns all pending requests for a user.
	 * 
	 * @param username Username of the user.
	 * @return List of pending requests.
	 */
	public List<PendingRequestDTO> GetPendingRequests(String username) {
		Wis2User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new NotFoundException("User not found"));

		List<PendingRequestDTO> pendingRequests = GetCourseCreationRequests(user);
		pendingRequests.addAll(GetCourseRegistrationRequests(user));
		pendingRequests.addAll(GetRoomCreationRequests(user));

		return pendingRequests;
	}

	/**
	 * Returns all pending course creation requests for a user.
	 * 
	 * @param user The user.
	 * @return List of pending course creation requests.
	 */
	private List<PendingRequestDTO> GetCourseCreationRequests(Wis2User user) {
		return courseRepository.findShortcutsBySupervisor_UsernameAndStatus(user.getUsername(), RequestStatus.PENDING)
				.stream()
				.map(shortcut -> new PendingRequestDTO(shortcut, PendingRequestType.COURSE_CREATION))
				.collect(Collectors.toList());
	}

	/**
	 * Returns all pending course registration requests for a user.
	 * 
	 * @param user The user.
	 * @return List of pending course registration requests.
	 */
	private List<PendingRequestDTO> GetCourseRegistrationRequests(Wis2User user) {
		return courseRepository.findPendingCourseShortcutsByUsername(user.getUsername())
				.stream()
				.map(shortcut -> new PendingRequestDTO(shortcut, PendingRequestType.COURSE_REGISTRATION))
				.collect(Collectors.toList());
	}

	/**
	 * Returns all pending room creation requests for a user.
	 * 
	 * @param user The user.
	 * @return List of pending room creation requests.
	 */
	private List<PendingRequestDTO> GetRoomCreationRequests(Wis2User user) {
		return roomRequestRepository.findRoomCreationRequestsForUser(user.getId())
				.stream()
				.map(roomName -> new PendingRequestDTO(roomName, PendingRequestType.ROOM_CREATION))
				.collect(Collectors.toList());
	}

	public OfficeShortcutDTO GetOfficeShortcut(String username) {
		Wis2User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new NotFoundException("User not found"));

		if (user.getOffice() == null) {
			return new OfficeShortcutDTO(null);
		}

		return new OfficeShortcutDTO(user.getOffice().getShortcut());
	}
}