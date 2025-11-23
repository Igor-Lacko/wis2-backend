package IIS.wis2_backend.Controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;

import IIS.wis2_backend.DTO.Request.Room.OfficeShortcutDTO;
import IIS.wis2_backend.DTO.Request.User.UpdateUserRequest;
import IIS.wis2_backend.DTO.Response.Course.UserCoursesDTO;
import IIS.wis2_backend.DTO.Response.User.PendingRequestDTO;
import IIS.wis2_backend.DTO.Response.User.TeacherDTO;
import IIS.wis2_backend.DTO.Response.User.UserDTO;
import IIS.wis2_backend.DTO.Response.User.VerySmallUserDTO;
import IIS.wis2_backend.Exceptions.ExceptionTypes.UnauthorizedException;
import IIS.wis2_backend.Services.UserService;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestParam;


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
     * @param username Username of the user.
     */
    @GetMapping("/public/{username}")
    public TeacherDTO GetPublicProfile(@PathVariable String username) {
        return userService.GetTeacherPublicProfile(username);
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



    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable long id, @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            UserDTO user = userService.GetUserById(id);

            if (!user.getUsername().equals(userDetails.getUsername())) {
                return ResponseEntity.status(HttpStatus.OK).build();
            }

            return ResponseEntity.ok(userService.updateUser(id, request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/teachers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VerySmallUserDTO>> getAllTeachers() {
        return ResponseEntity.ok(userService.getAllTeachers());
    }

    @GetMapping("/teachers/without-office")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VerySmallUserDTO>> getTeachersWithoutOffice() {
        return ResponseEntity.ok(userService.getTeachersWithoutOffice());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/promote")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> promoteUser(@PathVariable Long id) {
        userService.promoteUser(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Finds users matching the given query in their username, first name, or last name.
     * 
     * @param query The search query.
     * @return A list of users matching the query.
     */
    @GetMapping("/by-name")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<VerySmallUserDTO>> FindUsersMatchingQuery(@RequestParam String query) {
        List<VerySmallUserDTO> users = userService.GetUsersByNamePart(query);
        return ResponseEntity.ok(users);
    }

    /**
     * Returns all pending requests for a user.
     * 
     * @param authentication Authentication object containing user details.
     */
    @GetMapping("/pending-requests")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PendingRequestDTO>> GetPendingRequests(Authentication authentication) {
        List<PendingRequestDTO> pendingRequests = userService.GetPendingRequests(authentication.getName());
        return ResponseEntity.ok(pendingRequests);
    }

    /**
     * Returns the user's office's shortcut.
     * 
     * @param authentication contains username
     */
    @GetMapping("/office-shortcut")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OfficeShortcutDTO> GetUserOfficeShortcut(Authentication authentication) {
        OfficeShortcutDTO officeShortcut = userService.GetOfficeShortcut(authentication.getName());
        return ResponseEntity.ok(officeShortcut);
    }
    
}