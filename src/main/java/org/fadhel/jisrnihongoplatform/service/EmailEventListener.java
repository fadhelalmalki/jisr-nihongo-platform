package org.fadhel.jisrnihongoplatform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fadhel.jisrnihongoplatform.event.CertificateIssuedEvent;
import org.fadhel.jisrnihongoplatform.event.EnrollmentCreatedEvent;
import org.fadhel.jisrnihongoplatform.event.UserRegisteredEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "app.mail", name = "enabled", havingValue = "true", matchIfMissing = true)
public class EmailEventListener {

    private final EmailService emailService;

    // to welcome a newly registered learner once their account is committed to the database
    @Async("emailTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onUserRegistered(UserRegisteredEvent event) {

        Map<String, Object> variables = new HashMap<>();
        variables.put("userName", event.userName());
        variables.put("japaneseLevel", event.japaneseLevel());
        variables.put("learningGoal", event.learningGoal());

        emailService.sendHtmlEmailWithLogo(
                event.userEmail(),
                "ようこそ！ Welcome to Jisr Nihongo Platform",
                "welcome-email",
                variables);
    }

    // to confirm a course enrollment once it is committed to the database
    @Async("emailTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onEnrollmentCreated(EnrollmentCreatedEvent event) {

        Map<String, Object> variables = new HashMap<>();
        variables.put("userName", event.userName());
        variables.put("courseTitle", event.courseTitle());
        variables.put("instructorName", event.instructorName());
        variables.put("courseLevel", event.courseLevel());

        emailService.sendHtmlEmailWithLogo(
                event.userEmail(),
                "Course Registration Complete！ Enrollment Confirmed: " + event.courseTitle(),
                "enrollment-email",
                variables);
    }

    // to deliver a certificate once it is committed to the database
    @Async("emailTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onCertificateIssued(CertificateIssuedEvent event) {

        Map<String, Object> variables = new HashMap<>();
        variables.put("userName", event.userName());
        variables.put("courseTitle", event.courseTitle());
        variables.put("certificateNumber", event.certificateNumber());
        variables.put("issuedAt", event.issuedAt());

        emailService.sendHtmlEmailWithLogo(
                event.userEmail(),
                "おめでとうございます！ Your Course Certificate: " + event.certificateNumber(),
                "certificate-email",
                variables);
    }

}
