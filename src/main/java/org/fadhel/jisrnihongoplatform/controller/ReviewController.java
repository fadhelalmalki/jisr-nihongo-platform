package org.fadhel.jisrnihongoplatform.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fadhel.jisrnihongoplatform.dto.ApiResponse;
import org.fadhel.jisrnihongoplatform.exception.ApiException;
import org.fadhel.jisrnihongoplatform.model.Review;
import org.fadhel.jisrnihongoplatform.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;


    // to get all reviews
    @GetMapping
    public ResponseEntity<?> getAllReviews() {
        return ResponseEntity.status(200).body(reviewService.getAllReviews());
    }

    // to get a review by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getReviewById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(reviewService.getReviewById(id));
    }

    // to add a review
    @PostMapping
    public ResponseEntity<ApiResponse> addReview(@Valid @RequestBody Review review) {
        reviewService.addReview(review);
        return ResponseEntity.status(201).body(new ApiResponse("Review submitted successfully"));
    }

    // to update a review
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateReview(@PathVariable Integer id, @Valid @RequestBody Review review) {
        reviewService.updateReview(id, review);
        return ResponseEntity.status(200).body(new ApiResponse("Review updated successfully"));
    }

    // to delete a review
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteReview(@PathVariable Integer id) {
        reviewService.deleteReview(id);
        return ResponseEntity.status(200).body(new ApiResponse("Review deleted successfully"));
    }

    // 11 outOf 15 to retrieve all user reviews and ratings for a specific course.
    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getReviewsByCourse(@PathVariable Integer courseId) {
        return ResponseEntity.status(200).body(reviewService.getReviewsByCourse(courseId));
    }

    // 12 outOf 15 to filter course feedback by rating score
    @GetMapping("/rating/{rating}")
    public ResponseEntity<?> getReviewsByRating(@PathVariable Integer rating) {
        return ResponseEntity.status(200).body(reviewService.getReviewsByRating(rating));
    }
}
