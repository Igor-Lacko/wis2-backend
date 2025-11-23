package IIS.wis2_backend.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import IIS.wis2_backend.DTO.Request.Admin.SupervisorAssignmentDTO;
import IIS.wis2_backend.DTO.Request.Admin.ToggleAccountDTO;
import IIS.wis2_backend.DTO.Response.Admin.AdminStatsDTO;
import IIS.wis2_backend.DTO.Response.Course.AdminCourseDTO;
import IIS.wis2_backend.Services.AdminService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminStatsDTO getAdminStats() {
        return adminService.getAdminStats();
    }

    /**
     * Returns a list of all courses for admin view.
     */
    @GetMapping("/courses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminCourseDTO>> getCourses () {
        return ResponseEntity.ok(adminService.getCourses());
    }

    /**
     * Assigns a supervisor to a course.
     * 
     * @param courseId       ID of the course.
     * @param supervisorUsername Username of the supervisor to assign.
     */
    @PostMapping("/assign-supervisor")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignSupervisor(@RequestBody @Valid SupervisorAssignmentDTO dto) {
        adminService.assignSupervisor(dto.getCourseId(), dto.getSupervisorUsername());
        return ResponseEntity.ok().build();
    }

    /**
     * Activates or deactivates an account.
     * 
     * @param userId ID of the user.
     */
    @PostMapping("/toggle-account")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> toggleAccount(@RequestBody @Valid ToggleAccountDTO dto) {
        adminService.toggleAccount(dto.getUserId());
        return ResponseEntity.ok().build();
    }
}
