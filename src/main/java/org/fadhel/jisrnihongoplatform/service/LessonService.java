package org.fadhel.jisrnihongoplatform.service;


import lombok.RequiredArgsConstructor;
import org.fadhel.jisrnihongoplatform.exception.ApiException;
import org.fadhel.jisrnihongoplatform.model.Lesson;
import org.fadhel.jisrnihongoplatform.model.User;
import org.fadhel.jisrnihongoplatform.repository.CourseRepository;
import org.fadhel.jisrnihongoplatform.repository.LessonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;

    // to get all lessons
    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    // to get a lesson by id
    public Lesson getLessonById(Integer id) {

        Lesson existing = lessonRepository.findLessonById(id);
        if (existing == null) {
            throw new ApiException("Lesson not found");
        }

        return existing;
    }

    // to add a lesson
    public void addLesson(Lesson lesson) {
        if (courseRepository.findCourseById(lesson.getCourseId()) == null) {
            throw new ApiException("Course not found");
        }
        lessonRepository.save(lesson);
    }

    // to update a lesson
    public void updateLesson(Integer id, Lesson lesson) {
        Lesson existing = lessonRepository.findLessonById(id);
        if (existing == null) {
            throw new ApiException("Lesson not found");
        }
        if (courseRepository.findCourseById(lesson.getCourseId()) == null) {
            throw new ApiException("Course not found");
        }
        existing.setTitle(lesson.getTitle());
        existing.setDescription(lesson.getDescription());
        existing.setType(lesson.getType());
        existing.setVideoUrl(lesson.getVideoUrl());
        existing.setLessonOrder(lesson.getLessonOrder());
        lessonRepository.save(existing);
    }

    // to delete a lesson
    public void deleteLesson(Integer id) {
        Lesson lesson = lessonRepository.findLessonById(id);
        if (lesson == null) {
            throw new ApiException("Lesson not found");
        }
        lessonRepository.delete(lesson);
    }

    // 5 outOf 15 to fetch structured curriculum lessons for a course, sorted in order
    public List<Lesson> getLessonsByCourse(Integer courseId) {
        if (courseRepository.findCourseById(courseId) == null) {
            throw new ApiException("Course not found");
        }
        return lessonRepository.findLessonsByCourseIdOrderByLessonOrderAsc(courseId);
    }
}
