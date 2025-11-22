package IIS.wis2_backend.Controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import IIS.wis2_backend.DTO.Response.Room.AvailableRoomDTO;
import IIS.wis2_backend.DTO.Request.Room.CreateRoomRequest;
import IIS.wis2_backend.DTO.Request.Room.OfficeAssignmentRequest;
import IIS.wis2_backend.DTO.Response.Room.OfficeAssignmentDTO;
import IIS.wis2_backend.DTO.Response.Room.RoomRequestDTO;
import IIS.wis2_backend.DTO.Response.Room.RoomDetailsDTO;
import IIS.wis2_backend.Services.RoomService;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public List<RoomRequestDTO> getPendingRooms() {
        return roomService.getPendingRooms();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<RoomDetailsDTO> getAllRooms() {
        return roomService.getAllRooms();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomDetailsDTO> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        RoomDetailsDTO createdRoom = roomService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
    }

    @PutMapping("/offices/{officeId}/teachers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OfficeAssignmentDTO> assignOffice(@PathVariable Long officeId,
            @Valid @RequestBody OfficeAssignmentRequest request) {
        OfficeAssignmentDTO assignment = roomService.assignOffice(officeId, request);
        return ResponseEntity.ok(assignment);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public void approveRoom(@PathVariable Long id) {
        roomService.approveRoom(id);
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public void rejectRoom(@PathVariable Long id) {
        roomService.rejectRoom(id);
    }

    /**
     * Get available rooms between the specified start and end times.
     * 
     * @param start Start time.
     * @param end   End time.
     * @return List of available rooms.
     */
    @GetMapping("/available")
    public ResponseEntity<List<AvailableRoomDTO>> GetAvailableRooms(@RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        List<AvailableRoomDTO> availableRooms = roomService.GetAvailableLectureRooms(start, end);
        return ResponseEntity.ok(availableRooms);
    }

}
