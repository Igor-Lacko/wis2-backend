package IIS.wis2_backend.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import IIS.wis2_backend.Services.MockDBService;


@RestController
public class UserController {

    @GetMapping(value = "/student/{studentId}")
    public String getTestData(@PathVariable Integer studentId) {
        return "Student ID: " + studentId;
    }

    @GetMapping("/test")
    @Autowired
    public int test(MockDBService mockDBService) {
        mockDBService.InsertMockUsers();
        return 69420;
    }
}