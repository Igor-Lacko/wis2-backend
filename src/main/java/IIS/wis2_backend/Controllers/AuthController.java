package IIS.wis2_backend.Controllers;

import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import IIS.wis2_backend.DTO.Auth.SuccesfulLoginDTO;
import IIS.wis2_backend.DTO.Auth.LoginDTO;
import IIS.wis2_backend.DTO.Auth.RegisterDTO;
import IIS.wis2_backend.DTO.User.RegisterResponseDTO;
import IIS.wis2_backend.Services.AuthService;
import IIS.wis2_backend.Services.UserService;
import jakarta.validation.Valid;

/**
 * Controller for handling authentication related requests.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    /**
     * Service for authentication related operations.
     */
    private final AuthService authService;

    /**
     * This is here to get user ID and role.
     */
    private final UserService userService;

    /**
     * Constructor for AuthController.
     * 
     * @param authService Service for authentication related operations.
     */
    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    /**
     * Endpoint to register a new user.
     * 
     * @param registerDTO DTO containing the registration details.
     * @return ResponseEntity with UserDTO of the newly registered user.
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> Register(@Valid @RequestBody RegisterDTO registerDTO) {
        RegisterResponseDTO response = authService.RegisterUser(registerDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to login a user.
     * 
     * @param loginDTO DTO containing the login details.
     */
    @PostMapping("/login")
    public ResponseEntity<SuccesfulLoginDTO> Login(@Valid @RequestBody LoginDTO loginDTO) {
        // Authenticate user
        String token = authService.LoginUser(loginDTO);

        // Create cookie from token
        ResponseCookie cookie = ResponseCookie.from("JWT", token)
            .httpOnly(true)
            .path("/")
            .maxAge(24 * 60 * 60)
            .build();

        // Return these to the client
        String username = loginDTO.getUsername();
        Pair<Long, String> idAndRole = userService.GetUserIdAndRole(username);
        Long id = idAndRole.getFirst();
        String role = idAndRole.getSecond();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(new SuccesfulLoginDTO(id, username, role));
    }
}