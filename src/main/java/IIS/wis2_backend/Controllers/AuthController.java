package IIS.wis2_backend.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import IIS.wis2_backend.DTO.User.RegisterDTO;
import IIS.wis2_backend.DTO.User.UserDTO;
import IIS.wis2_backend.Services.AuthService;
import jakarta.validation.Valid;

/**
 * Controller for handling authentication related requests.
 */
@RestController
public class AuthController {
    /**
     * Service for authentication related operations.
     */
    private final AuthService authService;

    /**
     * Constructor for AuthController.
     * 
     * @param authService Service for authentication related operations.
     */
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint to register a new user.
     * 
     * @param registerDTO DTO containing the registration details.
     * @return ResponseEntity with UserDTO of the newly registered user.
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> Register(@Valid RegisterDTO registerDTO) {
        UserDTO userDTO = new UserDTO(authService.RegisterUser(registerDTO));
        return ResponseEntity.ok(userDTO);
    }
}