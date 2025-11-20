package IIS.wis2_backend.Repositories.Room;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import IIS.wis2_backend.Enum.RequestStatus;
import IIS.wis2_backend.Models.Room.RoomRequest;

@Repository
public interface RoomRequestRepository extends JpaRepository<RoomRequest, Long> {
    List<RoomRequest> findByStatus(RequestStatus status);
    long countByStatus(RequestStatus status);
}
