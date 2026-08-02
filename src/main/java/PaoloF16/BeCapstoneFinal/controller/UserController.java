package PaoloF16.BeCapstoneFinal.controller;

import PaoloF16.BeCapstoneFinal.entities.Role;
import PaoloF16.BeCapstoneFinal.entities.User;
import PaoloF16.BeCapstoneFinal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserService userService;

    // USERS
    @GetMapping("/users")
    public List<User> getUsers() { return userService.getAllUsers(); }

    @PostMapping("/users")
    public User createUser(@RequestBody User user) { return userService.createUser(user); }

    @PutMapping("/users/{id}")
    public User updateUser(@PathVariable UUID id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @PatchMapping("/users/{id}/toggle-status")
    public User toggleStatus(@PathVariable UUID id) {
        return userService.toggleUserStatus(id);
    }

}