package org.fadhel.jisrnihongoplatform.repository;


import org.fadhel.jisrnihongoplatform.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    Course findCourseById(Integer id);
    List<Course> findCoursesByLevel(String level);
    List<Course> findCoursesByInstructorId(Integer instructorId);
    List<Course> findCoursesByPriceLessThanEqual(BigDecimal maxPrice);

}
