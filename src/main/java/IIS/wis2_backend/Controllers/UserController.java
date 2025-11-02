package IIS.wis2_backend.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import IIS.wis2_backend.DTO.User.TeacherDTO;
import IIS.wis2_backend.Services.UserService;


/**
 * Controller for user-related requests.
 */
@RestController
@RequestMapping("/user")
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

    /**
     * Getter for a public profile by user id.
     * 
     * @param id User id.
     */
    @GetMapping("/public/{id}")
    public TeacherDTO GetPublicProfile(@PathVariable long id) {
        return userService.GetTeacherPublicProfile(id);
    }
}