package org.fadhel.jisrnihongoplatform.service;


import lombok.RequiredArgsConstructor;
import org.fadhel.jisrnihongoplatform.event.CertificateIssuedEvent;
import org.fadhel.jisrnihongoplatform.exception.ApiException;
import org.fadhel.jisrnihongoplatform.model.Certificate;
import org.fadhel.jisrnihongoplatform.model.Course;
import org.fadhel.jisrnihongoplatform.model.Enrollment;
import org.fadhel.jisrnihongoplatform.model.User;
import org.fadhel.jisrnihongoplatform.repository.CertificateRepository;
import org.fadhel.jisrnihongoplatform.repository.CourseRepository;
import org.fadhel.jisrnihongoplatform.repository.EnrollmentRepository;
import org.fadhel.jisrnihongoplatform.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final AdminService adminService;
    private final ApplicationEventPublisher eventPublisher;


    // to get all certificates (admin-only)
    public List<Certificate> getAllCertificates(Integer adminId) {
        adminService.verifyAdmin(adminId);
        return certificateRepository.findAll();
    }

    // to get a certificate by id (admin-only)
    public Certificate getCertificateById(Integer id, Integer adminId) {

        adminService.verifyAdmin(adminId);

        Certificate existing = certificateRepository.findCertificateById(id);
        if (existing == null) {
            throw new ApiException("Certificate not found");
        }

        return existing;
    }

    // to add a certificate (admin-only)
    @Transactional
    public void addCertificate(Certificate certificate, Integer adminId) {
        adminService.verifyAdmin(adminId);

        // Check if target enrollment exists
        Enrollment enrollment = enrollmentRepository.findEnrollmentById(certificate.getEnrollmentId());
        if (enrollment == null) {
            throw new ApiException("Enrollment not found for ID: " + certificate.getEnrollmentId());
        }

        // Check if enrollment already has a certificate
        if (certificateRepository.findByEnrollmentId(certificate.getEnrollmentId()).isPresent()) {
            throw new ApiException("A certificate has already been issued for this enrollment");
        }

        if (certificate.getIssuedAt() == null) {
            certificate.setIssuedAt(LocalDateTime.now());
        }

        certificateRepository.save(certificate);

        // to notify the learner of the manually issued certificate
        User user = userRepository.findUserById(enrollment.getUserId());
        Course course = courseRepository.findCourseById(enrollment.getCourseId());
        if (user != null && course != null) {
            eventPublisher.publishEvent(new CertificateIssuedEvent(
                    user.getName(),
                    user.getEmail(),
                    course.getTitle(),
                    certificate.getCertificateNumber(),
                    certificate.getIssuedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        }
    }

    // to update a certificate (admin-only)
    public void updateCertificate(Integer id, Certificate certificate, Integer adminId) {
        adminService.verifyAdmin(adminId);

        Certificate existing = certificateRepository.findCertificateById(id);
        if (existing == null) {
            throw new ApiException("Certificate not found");
        }

        // Check if target enrollment exists
        Enrollment enrollment = enrollmentRepository.findEnrollmentById(certificate.getEnrollmentId());
        if (enrollment == null) {
            throw new ApiException("Enrollment not found for ID: " + certificate.getEnrollmentId());
        }

        existing.setCertificateNumber(certificate.getCertificateNumber());

        certificateRepository.save(existing);
    }

    // to delete a certificate (admin-only)
    public void deleteCertificate(Integer id, Integer adminId) {

        adminService.verifyAdmin(adminId);

        Certificate cert = certificateRepository.findCertificateById(id);
        if (cert == null) {
            throw new ApiException("Certificate not found");
        }
        certificateRepository.delete(cert);
    }

    // 13 outOf 15 to get all certificates for a specific user
    public List<Certificate> getCertificatesByUserId(Integer userId) {
        if (userRepository.findUserById(userId) == null) {
            throw new ApiException("User not found");
        }

        return certificateRepository.findCertificatesByUserId(userId);
    }

    // 14 outOf 15 to verify certificate by a certificate number
    public Certificate verifyCertificate(String certificateNumber) {
        Certificate certificate = certificateRepository.findCertificateByCertificateNumber(certificateNumber);
        if (certificate == null) {
            throw new ApiException("Invalid certificate number");
        }
        return certificate;
    }
}
