package org.fadhel.jisrnihongoplatform.repository;


import org.fadhel.jisrnihongoplatform.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Integer> {
    Certificate findCertificateById(Integer id);
    Certificate findCertificateByCertificateNumber(String certificateNumber);
    Optional<Certificate> findByEnrollmentId(Integer enrollmentId);

    @Query("SELECT c FROM Certificate c WHERE c.enrollmentId IN (SELECT e.id FROM Enrollment e WHERE e.userId = :userId)")
    List<Certificate> findCertificatesByUserId(@Param("userId") Integer userId);
}
