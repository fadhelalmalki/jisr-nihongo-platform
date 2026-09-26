package org.fadhel.jisrnihongoplatform.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fadhel.jisrnihongoplatform.dto.ApiResponse;
import org.fadhel.jisrnihongoplatform.model.Admin;
import org.fadhel.jisrnihongoplatform.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // to get all admins (admin-only)
    @GetMapping
    public ResponseEntity<?> getAllAdmins(@RequestParam Integer adminId) {
        return ResponseEntity.status(200).body(adminService.getAllAdmins(adminId));
    }

    // to get an admin by id (admin-only)
    @GetMapping("/{id}")
    public ResponseEntity<?> getAdminById(@PathVariable Integer id, @RequestParam Integer adminId) {
        return ResponseEntity.status(200).body(adminService.getAdminById(adminId, id));
    }

    // to add an admin (admin-only) - (adminId optional for the first admin creation)
    @PostMapping
    public ResponseEntity<ApiResponse> addAdmin(@RequestParam(required = false) Integer adminId,
                                                @Valid @RequestBody Admin admin) {
        adminService.addAdmin(adminId, admin);
        return ResponseEntity.status(201).body(new ApiResponse("Admin created successfully"));
    }

    // to update an admin (admin-only)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateAdmin(@PathVariable Integer id,
                                                   @RequestParam Integer adminId,
                                                   @Valid @RequestBody Admin admin) {
        adminService.updateAdmin(id, adminId, admin);
        return ResponseEntity.status(200).body(new ApiResponse("Admin updated successfully"));
    }

    // to delete an admin (admin-only)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteAdmin(@PathVariable Integer id,
                                                   @RequestParam Integer adminId) {
        adminService.deleteAdmin(id, adminId);
        return ResponseEntity.status(200).body(new ApiResponse("Admin deleted successfully"));
    }

}
