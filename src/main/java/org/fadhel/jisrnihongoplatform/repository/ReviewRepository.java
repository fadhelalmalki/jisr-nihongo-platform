package org.fadhel.jisrnihongoplatform.repository;


import org.fadhel.jisrnihongoplatform.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    Review findReviewById(Integer id);
    List<Review> findReviewsByCourseId(Integer courseId);
    List<Review> findReviewsByRating(Integer rating);
}
