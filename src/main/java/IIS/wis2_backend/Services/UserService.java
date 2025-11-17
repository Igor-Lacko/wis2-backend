package IIS.wis2_backend.Services;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import IIS.wis2_backend.DTO.Request.Auth.RegisterDTO;
import IIS.wis2_backend.DTO.Response.NestedDTOs.CourseDTOForTeacher;
import IIS.wis2_backend.DTO.Response.NestedDTOs.OfficeDTOForTeacher;
import IIS.wis2_backend.DTO.Response.User.TeacherDTO;
import IIS.wis2_backend.DTO.Response.User.UserDTO;
import IIS.wis2_backend.Enum.Roles;
import IIS.wis2_backend.Exceptions.ExceptionTypes.InternalException;
import IIS.wis2_backend.Exceptions.ExceptionTypes.NotFoundException;
import IIS.wis2_backend.Models.User.*;
import IIS.wis2_backend.Repositories.CourseRepository;
import IIS.wis2_backend.Repositories.User.StudentRepository;
import IIS.wis2_backend.Repositories.User.TeacherRepository;
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
     * Repo for students, when setting course unit credit or grading.
     */
    private final StudentRepository studentRepository;

    /**
     * Repo for teachers, when assigning a teacher to a lesson/term/project, or
     * searching by office...
     */
    private final TeacherRepository teacherRepository;

    /**
     * Course repository, to get courses taught/supervised by a teacher.
     */
    private final CourseRepository courseRepository;

    /**
     * Constructor for UserService.
     * 
     * @param userRepository    User repository.
     * @param studentRepository Student repository.
     * @param teacherRepository Teacher repository.
     * @param courseRepository  Course repository.
     */
    public UserService(UserRepository userRepository, StudentRepository studentRepository,
            TeacherRepository teacherRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * Returns a public profile of a teacher by their user id.
     * 
     * @param userId The id of the user (teacher).
     * @return The public profile of the teacher.
     */
    public TeacherDTO GetTeacherPublicProfile(long userId) {
        Teacher teacher = teacherRepository.findById(userId).orElseThrow(
            () -> new NotFoundException("Teacher with this ID does not exist!")
        );

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

        return UserToDTO(userRepository.save(user));
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
    private TeacherDTO TeacherToDTO(Teacher teacher) {
        // Fetch supervised courses
        Set<CourseDTOForTeacher> supervisedCourses = courseRepository.findBySupervisor_Id(teacher.getId())
            .stream()
            .map(proj -> new CourseDTOForTeacher(
                proj.getId(),
                proj.getName(),
                proj.getShortcut()
            ))
            .collect(Collectors.toSet());

        // And taught courses!
        Set<CourseDTOForTeacher> taughtCourses = courseRepository.findByTeachers_Id(teacher.getId())
            .stream()
            .map(proj -> new CourseDTOForTeacher(
                proj.getId(),
                proj.getName(),
                proj.getShortcut()
            ))
            .collect(Collectors.toSet());

        // Also map office
        OfficeDTOForTeacher officeDTO = new OfficeDTOForTeacher(
                teacher.getOffice().getId(),
                teacher.getOffice().getShortcut()
        );

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

    public UserDTO GetUserById(long userId) {
        Wis2User user = userRepository.findById(userId).orElseThrow(
            () -> new NotFoundException("User with this ID does not exist!")
        );

        return UserToDTO(user);
    }
}