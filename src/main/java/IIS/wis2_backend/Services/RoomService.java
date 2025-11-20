package IIS.wis2_backend.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import IIS.wis2_backend.DTO.Response.Room.RoomDTO;
import IIS.wis2_backend.DTO.Response.Room.RoomRequestDTO;
import IIS.wis2_backend.Enum.RequestStatus;
import IIS.wis2_backend.Exceptions.ExceptionTypes.NotFoundException;
import IIS.wis2_backend.Models.Room.LectureRoom;
import IIS.wis2_backend.Models.Room.RoomRequest;
import IIS.wis2_backend.Repositories.Room.RoomRepository;
import IIS.wis2_backend.Repositories.Room.RoomRequestRepository;

@Service
public class RoomService {

    private final RoomRequestRepository roomRequestRepository;
    private final RoomRepository roomRepository;

    public RoomService(RoomRequestRepository roomRequestRepository, RoomRepository roomRepository) {
        this.roomRequestRepository = roomRequestRepository;
        this.roomRepository = roomRepository;
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

        LectureRoom room = new LectureRoom();
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
}
