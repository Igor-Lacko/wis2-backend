package IIS.wis2_backend.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import IIS.wis2_backend.Services.UserService;


/**
 * Controller for user-related requests.
 */
@RestController
public class UserController {
    /**
     * Service for user-related operations.
     */
    private final UserService userService;

    /**
     * Constructor for UserController.
     * 
     * @param userService Service for user-related operations.
     */
    public UserController(@Autowired UserService userService) {
        this.userService = userService;
    }
}