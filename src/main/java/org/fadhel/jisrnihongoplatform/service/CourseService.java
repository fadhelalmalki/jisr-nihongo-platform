package org.fadhel.jisrnihongoplatform.service;

import lombok.RequiredArgsConstructor;
import org.fadhel.jisrnihongoplatform.exception.ApiException;
import org.fadhel.jisrnihongoplatform.model.Course;
import org.fadhel.jisrnihongoplatform.model.User;
import org.fadhel.jisrnihongoplatform.repository.CourseRepository;
import org.fadhel.jisrnihongoplatform.repository.InstructorRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;

    // to get all courses
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // to get a course by id
    public Course getCourseById(Integer id) {

        Course existing = courseRepository.findCourseById(id);
        if (existing == null) {
            throw new ApiException("Course not found");
        }

        return existing;
    }

    // to add a course
    public void addCourse(Course course) {
        if (instructorRepository.findInstructorById(course.getInstructorId()) == null) {
            throw new ApiException("Instructor not found for ID: " + course.getInstructorId());
        }
        courseRepository.save(course);
    }

    // to update a course
    public void updateCourse(Integer id, Course course) {
        Course existing = courseRepository.findCourseById(id);
        if (existing == null) {
            throw new ApiException("Course not found");
        }
        if (instructorRepository.findInstructorById(course.getInstructorId()) == null) {
            throw new ApiException("Instructor not found for ID: " + course.getInstructorId());
        }
        existing.setTitle(course.getTitle());
        existing.setDescription(course.getDescription());
        existing.setLevel(course.getLevel());
        existing.setPrice(course.getPrice());
        existing.setDurationHours(course.getDurationHours());
        courseRepository.save(existing);
    }

    // to delete a course
    public void deleteCourse(Integer id) {
        Course course = courseRepository.findCourseById(id);
        if (course == null) {
            throw new ApiException("Course not found");
        }
        courseRepository.delete(course);
    }

    // 1 outOf 15 to search available Japanese courses filtered by JLPT level N5 through N1
    public List<Course> getCoursesByLevel(String level) {
        return courseRepository.findCoursesByLevel(level);
    }

    // 2 outOf 15 to fetch all courses created by a specific Japanese instructor.
    public List<Course> getCoursesByInstructor(Integer instructorId) {
        if (instructorRepository.findInstructorById(instructorId) == null) {
            throw new ApiException("Instructor not found");
        }
        return courseRepository.findCoursesByInstructorId(instructorId);
    }

    // 3 outOf 15 to filter courses under a maximum price.
    public List<Course> getCoursesByMaxPrice(BigDecimal maxPrice) {

        if (maxPrice == null || maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new ApiException("Maximum price must be non-null and non-negative.");
        }

        return courseRepository.findCoursesByPriceLessThanEqual(maxPrice);
    }

}
