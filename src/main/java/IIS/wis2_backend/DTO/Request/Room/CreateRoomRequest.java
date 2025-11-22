package IIS.wis2_backend.DTO.Request.Room;

import java.util.List;

import IIS.wis2_backend.Enum.RoomType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoomRequest {
    @NotBlank
    private String shortcut;

    @NotBlank
    private String building;

    @NotBlank
    private String floor;

    @NotNull
    @Min(1)
    private Integer capacity;

    @NotNull
    private RoomType roomType;

    private List<Long> occupantUserIds;
}
