package org.fadhel.jisrnihongoplatform.event;

public record CertificateIssuedEvent(
        String userName,
        String userEmail,
        String courseTitle,
        String certificateNumber,
        String issuedAt
) {
}
