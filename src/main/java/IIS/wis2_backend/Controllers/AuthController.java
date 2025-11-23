package IIS.wis2_backend.Controllers;

import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import IIS.wis2_backend.DTO.Request.Auth.LoginDTO;
import IIS.wis2_backend.DTO.Request.Auth.RegisterDTO;
import IIS.wis2_backend.DTO.Response.Auth.SuccesfulLoginDTO;
import IIS.wis2_backend.DTO.Response.User.RegisterResponseDTO;
import IIS.wis2_backend.Services.UserService;
import IIS.wis2_backend.Services.Account.AuthService;
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
        Pair<ResponseCookie, ResponseCookie> tokens = authService.LoginUser(loginDTO);
        ResponseCookie jwtCookie = tokens.getFirst();
        ResponseCookie refreshCookie = tokens.getSecond();

        // Return these to the client
        String username = loginDTO.getUsername();
        Pair<Long, String> idAndRole = userService.GetUserIdAndRole(username);
        Long id = idAndRole.getFirst();
        String role = idAndRole.getSecond();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new SuccesfulLoginDTO(id, username, role));
    }

    /**
     * Endpoint to refresh JWT token.
     */
    @PostMapping("/refresh")
    public ResponseEntity<Void> RefreshToken(@CookieValue(name = "REFRESH", required = false) String refreshToken) {
        Pair<ResponseCookie, ResponseCookie> tokens = authService.RefreshJWTToken(refreshToken);
        ResponseCookie jwtCookie = tokens.getFirst();
        ResponseCookie refreshCookie = tokens.getSecond();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .build();
    }

    /**
     * Endpoint to logout a user.
     * 
     * @param refreshToken The refresh token cookie.
     * @return ResponseEntity with status OK.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> Logout(@CookieValue(name = "REFRESH", required = false) String refreshToken) {
        authService.Logout(refreshToken);

        // Clearing cookies
        ResponseCookie jwtCookie = ResponseCookie.from("JWT", "")
                .httpOnly(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("REFRESH", "")
                .httpOnly(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .build();
    }
}