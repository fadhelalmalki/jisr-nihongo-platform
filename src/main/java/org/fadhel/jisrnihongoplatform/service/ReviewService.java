package org.fadhel.jisrnihongoplatform.service;


import lombok.RequiredArgsConstructor;
import org.fadhel.jisrnihongoplatform.exception.ApiException;
import org.fadhel.jisrnihongoplatform.model.Review;
import org.fadhel.jisrnihongoplatform.model.User;
import org.fadhel.jisrnihongoplatform.repository.CourseRepository;
import org.fadhel.jisrnihongoplatform.repository.ReviewRepository;
import org.fadhel.jisrnihongoplatform.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;


    // to get all reviews
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    // to get a review by id
    public Review getReviewById(Integer id) {

        Review existing = reviewRepository.findReviewById(id);
        if (existing == null) {
            throw new ApiException("Review not found");
        }

        return existing;
    }

    // to add a review
    public void addReview(Review review) {
        if (userRepository.findUserById(review.getUserId()) == null) {
            throw new ApiException("User not found");
        }
        if (courseRepository.findCourseById(review.getCourseId()) == null) {
            throw new ApiException("Course not found");
        }
        review.setCreatedAt(LocalDateTime.now());
        reviewRepository.save(review);
    }

    // to update a review
    public void updateReview(Integer id, Review review) {
        Review existing = reviewRepository.findReviewById(id);
        if (existing == null) {
            throw new ApiException("Review not found");
        }
        existing.setRating(review.getRating());
        existing.setComment(review.getComment());
        reviewRepository.save(existing);
    }

    // to delete a review
    public void deleteReview(Integer id) {
        Review review = reviewRepository.findReviewById(id);
        if (review == null) {
            throw new ApiException("Review not found");
        }
        reviewRepository.delete(review);
    }

    // 11 outOf 15 to retrieve all user reviews and ratings for a specific course.
    public List<Review> getReviewsByCourse(Integer courseId) {
        if (courseRepository.findCourseById(courseId) == null) {
            throw new ApiException("Course not found");
        }
        return reviewRepository.findReviewsByCourseId(courseId);
    }

    // 12 outOf 15 to filter course feedback by rating score
    public List<Review> getReviewsByRating(Integer rating) {
        return reviewRepository.findReviewsByRating(rating);
    }

}
