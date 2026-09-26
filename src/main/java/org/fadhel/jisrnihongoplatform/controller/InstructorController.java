package org.fadhel.jisrnihongoplatform.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fadhel.jisrnihongoplatform.dto.ApiResponse;
import org.fadhel.jisrnihongoplatform.dto.InstructorLicenseResponse;
import org.fadhel.jisrnihongoplatform.exception.ApiException;
import org.fadhel.jisrnihongoplatform.model.Instructor;
import org.fadhel.jisrnihongoplatform.service.InstructorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/instructors")
@RequiredArgsConstructor
public class InstructorController {

    private final InstructorService instructorService;

    // to get all instructors
    @GetMapping
    public ResponseEntity<?> getAllInstructors() {
        return ResponseEntity.status(200).body(instructorService.getAllInstructors());
    }

    // to get an instructor by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getInstructorById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(instructorService.getInstructorById(id));
    }

    // to add an instructor
    @PostMapping
    public ResponseEntity<ApiResponse> addInstructor(@Valid @RequestBody Instructor instructor) {
        instructorService.addInstructor(instructor);
        return ResponseEntity.status(201).body(new ApiResponse("Instructor created successfully"));
    }

    // to update an instructor
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateInstructor(@PathVariable Integer id, @Valid @RequestBody Instructor instructor) {
        instructorService.updateInstructor(id, instructor);
        return ResponseEntity.status(200).body(new ApiResponse("Instructor updated successfully"));
    }

    // to delete an instructor
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteInstructor(@PathVariable Integer id) {
        instructorService.deleteInstructor(id);
        return ResponseEntity.status(200).body(new ApiResponse("Instructor deleted successfully"));
    }

    // 4 outOf 15 to retrieve the verified freelance certificate details for a specific instructor
    @GetMapping("/{id}/freelance-license")
    public ResponseEntity<InstructorLicenseResponse> getInstructorFreelanceLicense(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(instructorService.getInstructorFreelanceLicense(id));
    }
}
