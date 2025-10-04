package IIS.wis2_backend.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import IIS.wis2_backend.Services.AuthService;

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
}