package org.fadhel.jisrnihongoplatform.event;

public record EnrollmentCreatedEvent(
        String userName,
        String userEmail,
        String courseTitle,
        String courseLevel,
        String instructorName
) {
}
