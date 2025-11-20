package IIS.wis2_backend.Controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import IIS.wis2_backend.DTO.Response.Course.UserCoursesDTO;
import IIS.wis2_backend.DTO.Response.User.TeacherDTO;
import IIS.wis2_backend.DTO.Response.User.UserDTO;
import IIS.wis2_backend.Exceptions.ExceptionTypes.UnauthorizedException;
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
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Logout a user by id.
     * 
     * @param id
     * @return
     */
    @GetMapping("/logout")
    public ResponseEntity<Integer> Logout() {
        // Invalidate jwt token and http cookie
        try {
            // Create an expired cookie for the same name so the client will remove it
            ResponseCookie expiredCookie = ResponseCookie.from("JWT", "")
                    .httpOnly(true)
                    .path("/")
                    .maxAge(0)
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
                    .body(0);
        } catch (Exception e) {
            // Something went wrong while creating the cookie
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(-1);
        }
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

    /**
     * Returns three sets: currently supervising courses, currently teaching and
     * currently enrolled courses.
     * 
     * @param userDetails Authenticated user details.
     * @param username    Username of the user whose courses are being fetched.
     */
    @GetMapping("/courses/{username}")
    public ResponseEntity<UserCoursesDTO> FetchUserCourses(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String username) {
        String authUsername = userDetails.getUsername();
        if (!authUsername.equals(username)) {
            throw new UnauthorizedException("You can't view other users' courses!");
        }

        return ResponseEntity.ok(userService.GetUserCourses(username));
    }

    // TODO move to shared nieco
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> GetUserById(@PathVariable long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            UserDTO user = userService.GetUserById(id);

            if (user.getUsername().equals(userDetails.getUsername())) {
                return ResponseEntity.ok(user);
            } else {
                // Not authorized to get other user's details
                return ResponseEntity.status(HttpStatus.OK).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("{id}")
    public String putMethodName(@PathVariable String id, @RequestBody UserDTO updatedUser) {
        // TODO: process PUT request

        return "Success";
    }
}