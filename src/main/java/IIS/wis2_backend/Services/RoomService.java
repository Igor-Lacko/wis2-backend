package IIS.wis2_backend.Services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import IIS.wis2_backend.DTO.Response.Room.AvailableRoomDTO;
import IIS.wis2_backend.DTO.Response.Room.RoomDTO;
import IIS.wis2_backend.DTO.Response.Room.RoomRequestDTO;
import IIS.wis2_backend.Enum.RequestStatus;
import IIS.wis2_backend.Exceptions.ExceptionTypes.NotFoundException;
import IIS.wis2_backend.Models.Room.StudyRoom;
import IIS.wis2_backend.Models.Room.RoomRequest;
import IIS.wis2_backend.Repositories.Room.RoomRepository;
import IIS.wis2_backend.Repositories.Room.RoomRequestRepository;
import IIS.wis2_backend.Repositories.Room.StudyRoomRepository;

@Service
public class RoomService {

    private final RoomRequestRepository roomRequestRepository;
    private final RoomRepository roomRepository;
    private final StudyRoomRepository studyRoomRepository;

    public RoomService(RoomRequestRepository roomRequestRepository, RoomRepository roomRepository,
            StudyRoomRepository studyRoomRepository) {
        this.roomRequestRepository = roomRequestRepository;
        this.roomRepository = roomRepository;
        this.studyRoomRepository = studyRoomRepository;
    }

    public List<RoomRequestDTO> getPendingRooms() {
        return roomRequestRepository.findByStatus(RequestStatus.PENDING).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void approveRoom(Long id) {
        RoomRequest request = roomRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            return;
        }

        StudyRoom room = new StudyRoom();
        room.setShortcut(request.getRoomName());
        room.setCapacity(request.getCapacity());
        room.setBuilding(request.getBuilding());
        room.setFloor(request.getFloor());

        roomRepository.save(room);

        request.setStatus(RequestStatus.APPROVED);
        roomRequestRepository.save(request);
    }

    public void rejectRoom(Long id) {
        RoomRequest request = roomRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room request not found"));
        request.setStatus(RequestStatus.REJECTED);
        roomRequestRepository.save(request);
    }

    private RoomRequestDTO convertToDTO(RoomRequest request) {
        RoomDTO roomDTO = RoomDTO.builder()
                .id(null)
                .name(request.getRoomName())
                .capacity(request.getCapacity())
                .building(request.getBuilding())
                .floor(request.getFloor())
                .build();

        return RoomRequestDTO.builder()
                .id(request.getId())
                .roomDetails(roomDTO)
                .requesterId(request.getRequesterId())
                .status(request.getStatus())
                .build();
    }

    /**
     * Get available rooms between the specified start and end times.
     * 
     * @param start Start time.
     * @param end   End time.
     * @return List of available rooms.
     */
    public List<AvailableRoomDTO> GetAvailableLectureRooms(LocalDateTime start, LocalDateTime end) {
        // Available means that a term doesn't exist with that room between start and
        return studyRoomRepository.findAvailableRoomDTOs(start, end);
    }
}
