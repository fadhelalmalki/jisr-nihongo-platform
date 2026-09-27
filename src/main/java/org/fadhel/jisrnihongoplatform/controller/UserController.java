package org.fadhel.jisrnihongoplatform.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fadhel.jisrnihongoplatform.dto.ApiResponse;
import org.fadhel.jisrnihongoplatform.dto.PhoneUpdateRequest;
import org.fadhel.jisrnihongoplatform.exception.ApiException;
import org.fadhel.jisrnihongoplatform.model.User;
import org.fadhel.jisrnihongoplatform.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // to get all users
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.status(200).body(userService.getAllUsers());
    }

    // to get a user by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        return  ResponseEntity.status(200).body(userService.getUserById(id));
    }

    // to add a user
    @PostMapping
    public ResponseEntity<ApiResponse> addUser(@Valid @RequestBody User user) {
        userService.addUser(user);
        return ResponseEntity.status(201).body(new ApiResponse("User registered successfully"));
    }

    // to update a user
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable Integer id, @Valid @RequestBody User user) {
        userService.updateUser(id, user);
        return ResponseEntity.status(200).body(new ApiResponse("User updated successfully"));
    }

    // to delete a user
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.status(200).body(new ApiResponse("User deleted successfully"));
    }

    // 15 outOf 15 to fetch all users by Japanese level (admin-only)
    @GetMapping("/level/{level}")
    public ResponseEntity<?> getUsersByLevel(@PathVariable String level, @RequestParam Integer requestingAdminId) {
        return ResponseEntity.status(200).body(userService.getUsersByLevel(level, requestingAdminId));
    }

    // Extra Endpoint: 14 to notify after phone number change through WhatsApp
    @PutMapping("/{id}/phone")
    public ResponseEntity<ApiResponse> updateUserPhone(@PathVariable Integer id,
                                                       @Valid @RequestBody PhoneUpdateRequest request) {
        boolean changed = userService.updateUserPhone(id, request.phone());

        return ResponseEntity.status(200).body(new ApiResponse(changed
                ? "Phone number updated, confirmation message queued"
                : "Phone number unchanged, no message sent"));
    }
}
