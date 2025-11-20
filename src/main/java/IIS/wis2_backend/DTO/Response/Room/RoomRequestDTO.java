package IIS.wis2_backend.DTO.Response.Room;

import IIS.wis2_backend.Enum.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomRequestDTO {
    private Long id;
    private RoomDTO roomDetails;
    private Long requesterId;
    private RequestStatus status;
}
