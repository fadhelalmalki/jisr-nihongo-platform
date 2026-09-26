package org.fadhel.jisrnihongoplatform.repository;


import org.fadhel.jisrnihongoplatform.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Integer> {

    Lesson findLessonById(Integer id);
    List<Lesson> findLessonsByCourseIdOrderByLessonOrderAsc(Integer courseId);
}
