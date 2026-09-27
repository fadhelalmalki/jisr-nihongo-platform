package org.fadhel.jisrnihongoplatform.service;

import org.fadhel.jisrnihongoplatform.event.CertificateIssuedEvent;
import org.fadhel.jisrnihongoplatform.event.EnrollmentCreatedEvent;
import org.fadhel.jisrnihongoplatform.event.UserRegisteredEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Verifies the event wiring: events published inside a transaction must reach the listener
 * and be delegated to EmailService on the async executor.
 */
@SpringBootTest
class EmailEventListenerIntegrationTest {

    @MockitoBean
    private EmailService emailService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void shouldDeliverWelcomeEmailEvent() {
        publish(new UserRegisteredEvent("Ahmed", "ahmed@example.com", "N5", "Pass JLPT N4", "+966512345678"));

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() ->
                verify(emailService).sendHtmlEmailWithLogo(
                        eq("ahmed@example.com"),
                        eq("ようこそ！ Welcome to Jisr Nihongo Platform"),
                        eq("welcome-email"),
                        any()));
    }

    @Test
    void shouldDeliverEnrollmentEmailEvent() {
        publish(new EnrollmentCreatedEvent("Fatima", "fatima@example.com", "Genki I", "N5", "Sato Sensei"));

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() ->
                verify(emailService).sendHtmlEmailWithLogo(
                        eq("fatima@example.com"),
                        eq("Course Registration Complete！ Enrollment Confirmed: Genki I"),
                        eq("enrollment-email"),
                        any()));
    }

    @Test
    void shouldDeliverCertificateEmailEvent() {
        publish(new CertificateIssuedEvent("Omar", "omar@example.com", "Genki II", "JISR-CERT-ABC123", "2026-09-27 14:30"));

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() ->
                verify(emailService).sendHtmlEmailWithLogo(
                        eq("omar@example.com"),
                        eq("おめでとうございます！ Your Course Certificate: JISR-CERT-ABC123"),
                        eq("certificate-email"),
                        any()));
    }

    @Test
    void shouldNotDeliverBeforeTheTransactionCommits() {
        // rolled back, so no listener should ever run for it
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            eventPublisher.publishEvent(new UserRegisteredEvent("Ghost", "ghost@example.com", "N5", "Nope", "+966500000000"));
            status.setRollbackOnly();
        });

        await().during(Duration.ofMillis(500)).atMost(Duration.ofSeconds(3)).untilAsserted(() ->
                verify(emailService, never()).sendHtmlEmailWithLogo(
                        eq("ghost@example.com"), any(), any(), any()));
    }

    private void publish(Object event) {
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> eventPublisher.publishEvent(event));
    }
}
