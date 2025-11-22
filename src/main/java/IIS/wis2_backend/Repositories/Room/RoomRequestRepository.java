package IIS.wis2_backend.Repositories.Room;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import IIS.wis2_backend.Enum.RequestStatus;
import IIS.wis2_backend.Models.Room.RoomRequest;

@Repository
public interface RoomRequestRepository extends JpaRepository<RoomRequest, Long> {
    List<RoomRequest> findByStatus(RequestStatus status);
    long countByStatus(RequestStatus status);

    /**
     * Finds room creation requests for a specific user with PENDING status.
     * 
     * @param userId ID of the user.
     * @return List of room names requested by the user.
     */
    @Query("SELECT rr.roomName FROM RoomRequest rr WHERE rr.requesterId = :userId AND rr.status = 'PENDING'")
    List<String> findRoomCreationRequestsForUser(@Param("userId") Long userId);   
}
