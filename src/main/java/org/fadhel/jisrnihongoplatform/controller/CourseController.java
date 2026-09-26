package org.fadhel.jisrnihongoplatform.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fadhel.jisrnihongoplatform.dto.ApiResponse;
import org.fadhel.jisrnihongoplatform.exception.ApiException;
import org.fadhel.jisrnihongoplatform.model.Course;
import org.fadhel.jisrnihongoplatform.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // to get all courses
    @GetMapping
    public ResponseEntity<?> getAllCourses() {
        return ResponseEntity.status(200).body(courseService.getAllCourses());
    }

    // to get a course by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(courseService.getCourseById(id));
    }

    // to add a course
    @PostMapping
    public ResponseEntity<ApiResponse> addCourse(@Valid @RequestBody Course course) {
        courseService.addCourse(course);
        return ResponseEntity.status(201).body(new ApiResponse("Course created successfully"));
    }

    // to update a course
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCourse(@PathVariable Integer id, @Valid @RequestBody Course course) {
        courseService.updateCourse(id, course);
        return ResponseEntity.status(200).body(new ApiResponse("Course updated successfully"));
    }

    // to delete a course
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCourse(@PathVariable Integer id) {
        courseService.deleteCourse(id);
        return ResponseEntity.status(200).body(new ApiResponse("Course deleted successfully"));
    }

    // 1 outOf 15 to search available Japanese courses filtered by JLPT level N5 through N1.
    @GetMapping("/level/{level}")
    public ResponseEntity<?> getCoursesByLevel(@PathVariable String level) {
        return ResponseEntity.status(200).body(courseService.getCoursesByLevel(level));
    }

    // 2 outOf 15 to fetch all courses created by a specific Japanese instructor.
    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<?> getCoursesByInstructor(@PathVariable Integer instructorId) {
        return ResponseEntity.status(200).body(courseService.getCoursesByInstructor(instructorId));
    }


    // 3 outOf 15 to filter courses under a maximum price.
    @GetMapping("/price")
    public ResponseEntity<?> getCoursesByMaxPrice(@RequestParam BigDecimal maxPrice) {
        return ResponseEntity.status(200).body(courseService.getCoursesByMaxPrice(maxPrice));
    }
}
