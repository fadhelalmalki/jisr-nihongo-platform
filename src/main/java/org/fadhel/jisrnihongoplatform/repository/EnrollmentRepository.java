package org.fadhel.jisrnihongoplatform.repository;


import org.fadhel.jisrnihongoplatform.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {

    Enrollment findEnrollmentById(Integer id);
    List<Enrollment> findEnrollmentsByUserId(Integer userId);
    Optional<Enrollment> findByUserIdAndCourseId(Integer userId, Integer courseId);
    List<Enrollment> findEnrollmentsByUserIdAndStatus(Integer userId, String status);
}
