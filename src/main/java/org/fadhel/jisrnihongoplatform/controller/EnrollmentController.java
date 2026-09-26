package org.fadhel.jisrnihongoplatform.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fadhel.jisrnihongoplatform.dto.ApiResponse;
import org.fadhel.jisrnihongoplatform.exception.ApiException;
import org.fadhel.jisrnihongoplatform.model.Enrollment;
import org.fadhel.jisrnihongoplatform.service.EnrollmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    // to get all enrollments
    @GetMapping
    public ResponseEntity<?> getAllEnrollments() {
        return ResponseEntity.status(200).body(enrollmentService.getAllEnrollments());
    }

    // to get an enrollment by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getEnrollmentById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(enrollmentService.getEnrollmentById(id));
    }

    // to add an enrollment
    @PostMapping
    public ResponseEntity<ApiResponse> enrollUser(@Valid @RequestBody Enrollment enrollment) {
        enrollmentService.enrollUser(enrollment);
        return ResponseEntity.status(201).body(new ApiResponse("Enrolled in course successfully"));
    }

    // to update an enrollment
    @PutMapping("/{id}/progress")
    public ResponseEntity<ApiResponse> updateProgress(@PathVariable Integer id, @RequestParam Integer progress) {
        enrollmentService.updateProgress(id, progress);
        return ResponseEntity.status(200).body(new ApiResponse("Progress updated successfully"));
    }

    // to delete an enrollment
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteEnrollment(@PathVariable Integer id) {
        enrollmentService.deleteEnrollment(id);
        return ResponseEntity.status(200).body(new ApiResponse("Enrollment deleted successfully"));
    }

    // 6 outOf 15 to complete learning progress percentage (triggers auto-completion when progress reaches 100)
    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse> completeEnrollment(@PathVariable Integer id) {
        enrollmentService.completeEnrollment(id);
        return ResponseEntity.status(200).body(new ApiResponse("Course completed and certificate issued!"));
    }

    // 7 outOf 15 to fetch all active and past course enrollments for a student.
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getEnrollmentsByUser(@PathVariable Integer userId) {
        return ResponseEntity.status(200).body(enrollmentService.getEnrollmentsByUser(userId));
    }

    // 8 outOf 15 to filter user enrollments to show only currently active courses
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<?> getActiveEnrollmentsByUser(@PathVariable Integer userId) {
        return ResponseEntity.status(200).body(enrollmentService.getActiveEnrollmentsByUser(userId));
    }

    // 9 outOf 15 to cancel user enrollment (admin-only)
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse> cancelEnrollment(@PathVariable Integer id, @RequestParam Integer requestingAdminId) {
        enrollmentService.cancelEnrollment(id, requestingAdminId);
        return ResponseEntity.status(200).body(new ApiResponse("Enrollment status successfully set to CANCELLED"));
    }

    // 10 outOf 15 to filter user enrollments to show only canceled courses
    @GetMapping("/user/{userId}/cancelled")
    public ResponseEntity<?> getCancelledEnrollmentsByUser(@PathVariable Integer userId) {
        return ResponseEntity.status(200).body(enrollmentService.getCancelledEnrollmentsByUser(userId));
    }
}
