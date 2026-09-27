package org.fadhel.jisrnihongoplatform.service;


import lombok.RequiredArgsConstructor;
import org.fadhel.jisrnihongoplatform.event.CertificateIssuedEvent;
import org.fadhel.jisrnihongoplatform.event.EnrollmentCreatedEvent;
import org.fadhel.jisrnihongoplatform.exception.ApiException;
import org.fadhel.jisrnihongoplatform.model.*;
import org.fadhel.jisrnihongoplatform.repository.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CertificateRepository certificateRepository;
    private final InstructorRepository instructorRepository;
    private final AdminService adminService;
    private final ApplicationEventPublisher eventPublisher;

    // to get all enrollments
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    // to get an enrollment by id
    public Enrollment getEnrollmentById(Integer id) {

        Enrollment existing = enrollmentRepository.findEnrollmentById(id);
        if (existing == null) {
            throw new ApiException("Enrollment not found");
        }

        return existing;
    }

    // to add an enrollment
    @Transactional
    public void enrollUser(Enrollment enrollment) {

        User user = userRepository.findUserById(enrollment.getUserId());
        if (user == null) {
            throw new ApiException("User not found");
        }

        Course course = courseRepository.findCourseById(enrollment.getCourseId());
        if (course == null) {
            throw new ApiException("Course not found");
        }
        if (enrollmentRepository.findByUserIdAndCourseId(enrollment.getUserId(), enrollment.getCourseId()).isPresent()) {
            throw new ApiException("User already enrolled in this course");
        }
        enrollment.setStatus("ACTIVE");
        enrollment.setProgress(0);
        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollmentRepository.save(enrollment);

        Instructor instructor = instructorRepository.findInstructorById(course.getInstructorId());

        eventPublisher.publishEvent(new EnrollmentCreatedEvent(
                user.getName(),
                user.getEmail(),
                course.getTitle(),
                course.getLevel(),
                instructor != null ? instructor.getName() : "Jisr Sensei"));

    }

    // to update an enrollment
    @Transactional
    public void updateProgress(Integer enrollmentId, Integer progress) {
        Enrollment enrollment = enrollmentRepository.findEnrollmentById(enrollmentId);
        if (enrollment == null) {
            throw new ApiException("Enrollment not found");
        }
        enrollment.setProgress(progress);

        boolean courseCompleted = progress >= 100;
        if (courseCompleted) {
            enrollment.setStatus("COMPLETED");
            enrollment.setCompletedAt(LocalDateTime.now());
        }
        enrollmentRepository.save(enrollment);

        if (courseCompleted) {
            issueCertificateIfNotExist(enrollment);
        }
    }

    // to delete an enrollment
    public void deleteEnrollment(Integer id) {
        Enrollment enrollment = enrollmentRepository.findEnrollmentById(id);
        if (enrollment == null) {
            throw new ApiException("Enrollment not found");
        }
        enrollmentRepository.delete(enrollment);
    }

    // 6 outOf 15 to complete learning progress percentage (triggers auto-completion when progress reaches 100)
    public void completeEnrollment(Integer enrollmentId) {
        updateProgress(enrollmentId, 100);
    }

    // 7 outOf 15 to fetch all active and past course enrollments for a user.
    public List<Enrollment> getEnrollmentsByUser(Integer userId) {
        if (userRepository.findUserById(userId) == null) {
            throw new ApiException("User not found");
        }
        return enrollmentRepository.findEnrollmentsByUserId(userId);
    }

    // 8 outOf 15 to filter user enrollments to show only currently active courses
    public List<Enrollment> getActiveEnrollmentsByUser(Integer userId) {
        if (userRepository.findUserById(userId) == null) {
            throw new ApiException("User not found");
        }
        return enrollmentRepository.findEnrollmentsByUserIdAndStatus(userId, "ACTIVE");
    }

    // 9 outOf 15 to cancel user enrollment (admin-only)
    public void cancelEnrollment(Integer id, Integer requestingAdminId) {

        adminService.verifyAdmin(requestingAdminId);

        Enrollment enrollment = enrollmentRepository.findEnrollmentById(id);
        if (enrollment == null) {
            throw new ApiException("Enrollment not found");
        }
        if ("CANCELLED".equals(enrollment.getStatus())) {
            throw new ApiException("Enrollment is already cancelled");
        }
        if("COMPLETED".equals(enrollment.getStatus())) {
            throw new ApiException("Enrollment is already completed");
        }
        enrollment.setStatus("CANCELLED");
        enrollmentRepository.save(enrollment);
    }

    // 10 outOf 15 to filter user enrollments to show only canceled courses
    public List<Enrollment> getCancelledEnrollmentsByUser(Integer userId) {
        if (userRepository.findUserById(userId) == null) {
            throw new ApiException("User not found");
        }
        return enrollmentRepository.findEnrollmentsByUserIdAndStatus(userId, "CANCELLED");
    }

    // Helper method to generate and issue a unique completion certificate for an enrollment if it doesn't already exist.
    private void issueCertificateIfNotExist(Enrollment enrollment) {

        if (certificateRepository.findByEnrollmentId(enrollment.getId()).isPresent()) {
            return;
        }

        String certNum = "JISR-CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Certificate cert = new Certificate();
        cert.setEnrollmentId(enrollment.getId());
        cert.setCertificateNumber(certNum);
        cert.setIssuedAt(LocalDateTime.now());
        certificateRepository.save(cert);

        publishCertificateIssuedEvent(enrollment, cert);
    }

    // Helper method to notify the learner of a new certificate by resolving the recipient and course from the enrollment.
    private void publishCertificateIssuedEvent(Enrollment enrollment, Certificate cert) {

        User user = userRepository.findUserById(enrollment.getUserId());
        Course course = courseRepository.findCourseById(enrollment.getCourseId());
        if (user == null || course == null) {
            return;
        }

        eventPublisher.publishEvent(new CertificateIssuedEvent(
                user.getName(),
                user.getEmail(),
                course.getTitle(),
                cert.getCertificateNumber(),
                cert.getIssuedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
    }
}
