package IIS.wis2_backend.Services;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import IIS.wis2_backend.DTO.Request.Auth.RegisterDTO;
import IIS.wis2_backend.DTO.Request.User.UpdateUserRequest;
import IIS.wis2_backend.DTO.Response.Course.UserCoursesDTO;
import IIS.wis2_backend.DTO.Response.NestedDTOs.CourseDTOForTeacher;
import IIS.wis2_backend.DTO.Response.NestedDTOs.OfficeDTOForTeacher;
import IIS.wis2_backend.DTO.Response.NestedDTOs.OverviewCourseDTO;
import IIS.wis2_backend.DTO.Response.Projections.OverviewCourseProjection;
import IIS.wis2_backend.DTO.Response.User.TeacherDTO;
import IIS.wis2_backend.DTO.Response.User.UserDTO;
import IIS.wis2_backend.Enum.Roles;
import IIS.wis2_backend.Enum.RequestStatus;
import IIS.wis2_backend.Exceptions.ExceptionTypes.InternalException;
import IIS.wis2_backend.Exceptions.ExceptionTypes.NotFoundException;
import IIS.wis2_backend.Exceptions.ExceptionTypes.UserAlreadyExistsException;
import IIS.wis2_backend.Models.Schedule;
import IIS.wis2_backend.Models.User.*;
import IIS.wis2_backend.Repositories.CourseRepository;
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
     * Constructor for UserService.
     * 
     * @param userRepository    User repository.
     * @param courseRepository  Course repository.
     */
    public UserService(UserRepository userRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * Returns a public profile of a teacher by their user id.
     * 
     * @param userId The id of the user (teacher).
     * @return The public profile of the teacher.
     */
    public TeacherDTO GetTeacherPublicProfile(long userId) {
        Wis2User teacher = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Teacher with this ID does not exist!"));

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
                .supervisedCourses(courseRepository.findBySupervisor_UsernameAndStatus(username, RequestStatus.APPROVED).stream()
                        .map(this::OverviewProjectionToDTO)
                        .collect(Collectors.toList()))
                .teachingCourses(courseRepository.findByTeachers_UsernameAndStatus(username, RequestStatus.APPROVED).stream()
                        .map(this::OverviewProjectionToDTO)
                        .collect(Collectors.toList()))
                .enrolledCourses(courseRepository.findDistinctByStudentCourses_Student_UsernameAndStatus(username, RequestStatus.APPROVED).stream()
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
        Set<CourseDTOForTeacher> supervisedCourses = courseRepository.findBySupervisor_IdAndStatus(teacher.getId(), RequestStatus.APPROVED)
                .stream()
                .map(proj -> new CourseDTOForTeacher(
                        proj.getId(),
                        proj.getName(),
                        proj.getShortcut()))
                .collect(Collectors.toSet());

        // And taught courses!
        Set<CourseDTOForTeacher> taughtCourses = courseRepository.findByTeachers_IdAndStatus(teacher.getId(), RequestStatus.APPROVED)
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
     * @return List of all users.
     */
    public java.util.List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> UserDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .birthday(user.getBirthday())
                        .telephoneNumber(user.getTelephoneNumber())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Delete a user by ID.
     * @param id User ID.
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    /**
     * Promote a user to ADMIN.
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
            if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
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
}