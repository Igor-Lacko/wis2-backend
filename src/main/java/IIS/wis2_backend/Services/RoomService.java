package IIS.wis2_backend.Services;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import IIS.wis2_backend.DTO.Request.Room.CreateRoomRequest;
import IIS.wis2_backend.DTO.Request.Room.OfficeAssignmentRequest;
import IIS.wis2_backend.DTO.Response.Room.AvailableRoomDTO;
import IIS.wis2_backend.DTO.Response.Room.OfficeAssignmentDTO;
import IIS.wis2_backend.DTO.Response.Room.RoomDTO;
import IIS.wis2_backend.DTO.Response.Room.RoomDetailsDTO;
import IIS.wis2_backend.DTO.Response.Room.RoomRequestDTO;
import IIS.wis2_backend.Enum.RequestStatus;
import IIS.wis2_backend.Enum.RoomType;
import IIS.wis2_backend.Exceptions.ExceptionTypes.AlreadySetException;
import IIS.wis2_backend.Exceptions.ExceptionTypes.NotFoundException;
import IIS.wis2_backend.Models.Room.Office;
import IIS.wis2_backend.Models.Room.Room;
import IIS.wis2_backend.Models.Room.StudyRoom;
import IIS.wis2_backend.Models.Room.RoomRequest;
import IIS.wis2_backend.Models.User.Wis2User;
import IIS.wis2_backend.Repositories.Room.OfficeRepository;
import IIS.wis2_backend.Repositories.Room.RoomRepository;
import IIS.wis2_backend.Repositories.Room.RoomRequestRepository;
import IIS.wis2_backend.Repositories.Room.StudyRoomRepository;
import IIS.wis2_backend.Repositories.User.UserRepository;

@Service
public class RoomService {

    private final RoomRequestRepository roomRequestRepository;
    private final RoomRepository roomRepository;
    private final StudyRoomRepository studyRoomRepository;
    private final OfficeRepository officeRepository;
    private final UserRepository userRepository;

    public RoomService(RoomRequestRepository roomRequestRepository, RoomRepository roomRepository,
            StudyRoomRepository studyRoomRepository, OfficeRepository officeRepository,
            UserRepository userRepository) {
        this.roomRequestRepository = roomRequestRepository;
        this.roomRepository = roomRepository;
        this.studyRoomRepository = studyRoomRepository;
        this.officeRepository = officeRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RoomDetailsDTO createRoom(CreateRoomRequest request) {
        roomRepository.findByShortcut(request.getShortcut()).ifPresent(room -> {
            throw new AlreadySetException("Room with this shortcut already exists");
        });

        Room savedRoom;
        if (request.getRoomType() == RoomType.STUDY_ROOM) {
            StudyRoom studyRoom = new StudyRoom();
            applyBaseRoomDetails(studyRoom, request);
            savedRoom = roomRepository.save(studyRoom);
        } else if (request.getRoomType() == RoomType.OFFICE) {
            Office office = new Office();
            applyBaseRoomDetails(office, request);
            savedRoom = officeRepository.save(office);
            if (request.getOccupantUserIds() != null && !request.getOccupantUserIds().isEmpty()) {
                updateOfficeAssignments(office, request.getOccupantUserIds());
            }
        } else {
            throw new IllegalArgumentException("Unsupported room type: " + request.getRoomType());
        }

        return mapToRoomDetails(savedRoom);
    }

    @Transactional
    public OfficeAssignmentDTO assignOffice(Long officeId, OfficeAssignmentRequest request) {
        Office office = officeRepository.findById(officeId)
                .orElseThrow(() -> new NotFoundException("Office not found"));

        List<Long> assignedUserIds = updateOfficeAssignments(office, request.getUserIds());

        return OfficeAssignmentDTO.builder()
                .officeId(office.getId())
                .officeShortcut(office.getShortcut())
                .assignedUserIds(assignedUserIds)
                .build();
    }

    @Transactional(readOnly = true)
    public List<RoomDetailsDTO> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::mapToRoomDetails)
                .collect(Collectors.toList());
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

    private void applyBaseRoomDetails(Room room, CreateRoomRequest request) {
        room.setShortcut(request.getShortcut());
        room.setBuilding(request.getBuilding());
        room.setFloor(request.getFloor());
        room.setCapacity(request.getCapacity());
    }

    private RoomDetailsDTO mapToRoomDetails(Room room) {
        RoomType type = (room instanceof Office) ? RoomType.OFFICE : RoomType.STUDY_ROOM;
        List<Long> occupantUserIds = Collections.emptyList();
        if (room instanceof Office) {
            Office office = (Office) room;
            if (office.getTeachers() != null && !office.getTeachers().isEmpty()) {
                occupantUserIds = office.getTeachers().stream()
                        .map(Wis2User::getId)
                        .sorted()
                        .collect(Collectors.toList());
            }
        }
        return RoomDetailsDTO.builder()
                .id(room.getId())
                .shortcut(room.getShortcut())
                .building(room.getBuilding())
                .floor(room.getFloor())
                .capacity(room.getCapacity())
                .roomType(type)
            .occupantUserIds(occupantUserIds)
                .build();
    }

    private List<Long> updateOfficeAssignments(Office office, List<Long> userIds) {
        List<Long> targetIds = sanitizeUserIds(userIds);
        LinkedHashSet<Long> targetSet = new LinkedHashSet<>(targetIds);

        List<Wis2User> currentlyAssigned = userRepository.findAllByOffice_Id(office.getId());
        if (!currentlyAssigned.isEmpty()) {
            List<Wis2User> toUnassign = currentlyAssigned.stream()
                    .filter(user -> !targetSet.contains(user.getId()))
                    .collect(Collectors.toList());
            toUnassign.forEach(user -> user.setOffice(null));
            if (!toUnassign.isEmpty()) {
                userRepository.saveAll(toUnassign);
            }
        }

        if (targetSet.isEmpty()) {
            office.setTeachers(new HashSet<>());
            officeRepository.save(office);
            return Collections.emptyList();
        }

        List<Wis2User> desiredUsers = userRepository.findAllById(targetSet);
        if (desiredUsers.size() != targetSet.size()) {
            throw new NotFoundException("One or more specified users do not exist");
        }

        desiredUsers.forEach(user -> user.setOffice(office));
        userRepository.saveAll(desiredUsers);
        office.setTeachers(new HashSet<>(desiredUsers));
        officeRepository.save(office);

        return List.copyOf(targetSet);
    }

    private List<Long> sanitizeUserIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyList();
        }

        LinkedHashSet<Long> uniqueIds = new LinkedHashSet<>();
        for (Long userId : userIds) {
            if (userId == null) {
                throw new IllegalArgumentException("User id cannot be null");
            }
            uniqueIds.add(userId);
        }
        return List.copyOf(uniqueIds);
    }
}
