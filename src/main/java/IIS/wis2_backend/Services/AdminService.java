package IIS.wis2_backend.Services;

import org.springframework.stereotype.Service;

import IIS.wis2_backend.DTO.Response.Admin.AdminStatsDTO;
import IIS.wis2_backend.Enum.RequestStatus;
import IIS.wis2_backend.Repositories.CourseRepository;
import IIS.wis2_backend.Repositories.Room.RoomRequestRepository;
import IIS.wis2_backend.Repositories.User.UserRepository;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final RoomRequestRepository roomRequestRepository;

    public AdminService(UserRepository userRepository, CourseRepository courseRepository, RoomRequestRepository roomRequestRepository) {
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
}
