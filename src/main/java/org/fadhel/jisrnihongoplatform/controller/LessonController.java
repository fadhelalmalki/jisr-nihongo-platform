package org.fadhel.jisrnihongoplatform.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fadhel.jisrnihongoplatform.dto.ApiResponse;
import org.fadhel.jisrnihongoplatform.exception.ApiException;
import org.fadhel.jisrnihongoplatform.model.Lesson;
import org.fadhel.jisrnihongoplatform.service.LessonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    // to get all lessons
    @GetMapping
    public ResponseEntity<?> getAllLessons() {
        return ResponseEntity.status(200).body(lessonService.getAllLessons());
    }

    // to get a lesson by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getLessonById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(lessonService.getLessonById(id));
    }

    // to add a lesson
    @PostMapping
    public ResponseEntity<ApiResponse> addLesson(@Valid @RequestBody Lesson lesson) {
        lessonService.addLesson(lesson);
        return ResponseEntity.status(201).body(new ApiResponse("Lesson created successfully"));
    }

    // to update a lesson
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateLesson(@PathVariable Integer id, @Valid @RequestBody Lesson lesson) {
        lessonService.updateLesson(id, lesson);
        return ResponseEntity.status(200).body(new ApiResponse("Lesson updated successfully"));
    }

    // to delete a lesson
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteLesson(@PathVariable Integer id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.status(200).body(new ApiResponse("Lesson deleted successfully"));
    }

    // 5 outOf 15 to fetch structured curriculum lessons for a course, sorted in order
    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getLessonsByCourse(@PathVariable Integer courseId) {
        return ResponseEntity.status(200).body(lessonService.getLessonsByCourse(courseId));
    }
}
