package org.fadhel.jisrnihongoplatform.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fadhel.jisrnihongoplatform.dto.ApiResponse;
import org.fadhel.jisrnihongoplatform.exception.ApiException;
import org.fadhel.jisrnihongoplatform.model.Certificate;
import org.fadhel.jisrnihongoplatform.service.CertificateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    // to get all certificates (admin-only)
    @GetMapping
    public ResponseEntity<?> getAllCertificates(@RequestParam Integer adminId) {
        return ResponseEntity.status(200).body(certificateService.getAllCertificates(adminId));
    }

    // to get a certificate by id (admin-only)
    @GetMapping("/{id}")
    public ResponseEntity<?> getCertificateById(@PathVariable Integer id, @RequestParam Integer adminId) {
        return ResponseEntity.status(200).body(certificateService.getCertificateById(id, adminId));
    }

    // to add a certificate (admin-only)
    @PostMapping
    public ResponseEntity<ApiResponse> addCertificate(@RequestParam Integer adminId,
                                                      @Valid @RequestBody Certificate certificate) {
        certificateService.addCertificate(certificate, adminId);
        return ResponseEntity.status(201).body(new ApiResponse("Certificate created successfully"));
    }

    // to update a certificate (admin-only)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCertificate(@PathVariable Integer id,
                                                         @RequestParam Integer adminId,
                                                         @Valid @RequestBody Certificate certificate) {
        certificateService.updateCertificate(id, certificate, adminId);
        return ResponseEntity.status(200).body(new ApiResponse("Certificate updated successfully"));
    }

    // to delete a certificate (admin-only)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCertificate(@PathVariable Integer id, @RequestParam Integer adminId) {
        certificateService.deleteCertificate(id, adminId);
        return ResponseEntity.status(200).body(new ApiResponse("Certificate deleted successfully"));
    }

    // 13 outOf 15 to get all certificates for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getCertificatesByUserId(@PathVariable Integer userId) {
        return ResponseEntity.status(200).body(certificateService.getCertificatesByUserId(userId));
    }

    // 14 outOf 15 to verify certificate by a certificate number
    @GetMapping("/verify/{certificateNumber}")
    public ResponseEntity<?> verifyCertificate(@PathVariable String certificateNumber) {
        return ResponseEntity.status(200).body(certificateService.verifyCertificate(certificateNumber));
    }
}
