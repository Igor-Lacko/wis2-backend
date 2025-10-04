package IIS.wis2_backend.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import IIS.wis2_backend.DTO.User.TeacherDTO;
import IIS.wis2_backend.DTO.User.UserDTO;
import IIS.wis2_backend.Models.User.*;
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
     * Constructor for UserService.
     * 
     * @param userRepository    User repository.
     * @param studentRepository Student repository.
     * @param teacherRepository Teacher repository.
     */
    @Autowired
    public UserService(UserRepository userRepository, StudentRepository studentRepository,
            TeacherRepository teacherRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    /**
     * Getter for all users.
     * 
     * @return List of all users.
     */
    public List<UserDTO> GetAllUsers() {
        return userRepository
                .findAll()
                .stream()
                .map(this::UserToDTO)
                .toList();
    }

    /**
     * Getter for user by ID.
     * 
     * @param id User ID.
     * @return UserDTO or null if not found.
     */
    public UserDTO GetUserById(Long id) {
        return userRepository
                .findById(id)
                .map(this::UserToDTO)
                .orElse(null);
    }

    /**
     * Create a new user.
     * 
     * @param userDTO DTO of the user to create.
     * @return DTO of the created user.
     */
    public UserDTO CreateUser(UserDTO userDTO) {
        Wis2User user = Wis2User.builder()
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .email(userDTO.getEmail())
                .build();
        Wis2User savedUser = userRepository.save(user);
        return UserToDTO(savedUser);
    }

    /**
     * Update an existing user with fields that don't necessarily have to be validated.
     * 
     * @param id      ID of the user to update.
     * @param userDTO DTO with updated user data.
     * @return Updated UserDTO or null if user not found.
     */
    public UserDTO UpdateUser(Long id, UserDTO userDTO) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setFirstName(userDTO.getFirstName());
                    existingUser.setLastName(userDTO.getLastName());
                    Wis2User updatedUser = userRepository.save(existingUser);
                    return UserToDTO(updatedUser);
                })
                .orElse(null);
    }

    /**
     * Delete an existing user.
     * 
     * @param id ID of the user to delete.
     * @return true if user was deleted, false if not found.
     */
    public boolean DeleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Getter for all teachers.
     * 
     * @return List of all teachers.
     */
    public List<TeacherDTO> GetAllTeachers() {
        return teacherRepository
                .findAll()
                .stream()
                .map(this::TeacherToDTO)
                .toList();
    }

    public List<TeacherDTO> GetTeachersByName (String name) {
        return teacherRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name)
                .stream()
                .map(this::TeacherToDTO)
                .toList();
    }

    /**
     * Getter for teacher by ID.
     * 
     * @param id Teacher ID.
     * @return TeacherDTO or null if not found.
     */
    public TeacherDTO GetTeacherById(Long id) {
        return teacherRepository
                .findById(id)
                .map(this::TeacherToDTO)
                .orElse(null);
    }

    /**
     * Gets all students.
     * 
     * @return List of all students.
     */
    public List<UserDTO> GetAllStudents() {
        return studentRepository
                .findAll()
                .stream()
                .map(this::UserToDTO)
                .toList();
    }

    /**
     * Gets a student by ID.
     * @param id Student ID.
     * @return UserDTO (so far) or null if not found.
     */
    public UserDTO GetStudentById(Long id) {
        return studentRepository
                .findById(id)
                .map(this::UserToDTO)
                .orElse(null);
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
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }

    /**
     * Convert Teacher entity to TeacherDTO.
     * 
     * @param teacher Teacher entity.
     * @return TeacherDTO.
     */
    private TeacherDTO TeacherToDTO(Teacher teacher) {
        return TeacherDTO.builder()
                .id(teacher.getId())
                .firstName(teacher.getFirstName())
                .lastName(teacher.getLastName())
                .email(teacher.getEmail())
                .office(teacher.getOffice().getShortcut())
                .build();
    }
}