package IIS.wis2_backend.Services;

import java.util.List;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import IIS.wis2_backend.DTO.Response.Admin.AdminStatsDTO;
import IIS.wis2_backend.DTO.Response.Course.AdminCourseDTO;
import IIS.wis2_backend.Enum.RequestStatus;
import IIS.wis2_backend.Models.User.Wis2User;
import IIS.wis2_backend.Repositories.CourseRepository;
import IIS.wis2_backend.Repositories.Room.RoomRequestRepository;
import IIS.wis2_backend.Repositories.User.UserRepository;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final RoomRequestRepository roomRequestRepository;

    public AdminService(UserRepository userRepository, CourseRepository courseRepository,
            RoomRequestRepository roomRequestRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.roomRequestRepository = roomRequestRepository;
    }

    public AdminStatsDTO getAdminStats() {
        return AdminStatsDTO.builder()
                .totalUsers(userRepository.count())
                .totalCourses(courseRepository.count())
                .pendingCourseRequests(courseRepository.countByStatus(RequestStatus.PENDING))
                .pendingRoomRequests(roomRequestRepository.countByStatus(RequestStatus.PENDING))
                .build();
    }

    public List<AdminCourseDTO> getCourses() {
        List<AdminCourseDTO> result = new ArrayList<>();
        for (var course : courseRepository.findByStatus(RequestStatus.APPROVED)) {
            Wis2User supervisor = course.getSupervisor();
            result.add(AdminCourseDTO.builder()
                    .id(course.getId())
                    .name(course.getName())
                    .shortcut(course.getShortcut())
                    .completedBy(course.getCompletedBy())
                    .capacity(course.getCapacity())
                    .supervisorUsername(supervisor != null ? supervisor.getUsername() : null)
                    .enrolledStudents((int) courseRepository.getEnrolledCountByCourseShortcut(course.getShortcut()))
                    .build());
        }
        return result;
    }

    public void assignSupervisor(Long courseId, String supervisorUsername) {
        var course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course with ID " + courseId + " not found"));

        if (course.getSupervisor() != null) {
            throw new IllegalArgumentException("Course already has a supervisor assigned");
        }

        // Check if the supervisor is not a student
        if (userRepository.existsByUsernameAndStudentCourses_Course_ShortcutAndStudentCourses_Status(supervisorUsername,
                course.getShortcut(), RequestStatus.APPROVED)) {
            throw new IllegalArgumentException(
                    "User with username " + supervisorUsername + " is enrolled as a student in this course");
        }

        var supervisor = userRepository.findByUsername(supervisorUsername).orElseThrow(
                () -> new IllegalArgumentException("Supervisor with username " + supervisorUsername + " not found"));

        // Set supervisor
        course.setSupervisor(supervisor);

        // Add supervisor to teachers list (Course owns this relationship)
        if (course.getTeachers() == null) {
            course.setTeachers(new java.util.HashSet<>());
        }
        course.getTeachers().add(supervisor);

        supervisor.getSupervisedCourses().add(course);
        supervisor.getTaughtCourses().add(course);

        courseRepository.save(course);
    }

    public void toggleAccount(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));

        user.setActivated(!user.isActivated());
        userRepository.save(user);
    }
}
