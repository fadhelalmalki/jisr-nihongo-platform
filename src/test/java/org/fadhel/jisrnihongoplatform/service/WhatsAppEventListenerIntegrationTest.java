package org.fadhel.jisrnihongoplatform.service;

import org.fadhel.jisrnihongoplatform.event.UserPhoneAddedEvent;
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
 * Verifies the WhatsApp event wiring: the phone added event must only reach the gateway
 * once the transaction that saved the number has committed.
 */
@SpringBootTest(properties = "app.whatsapp.enabled=true")
class WhatsAppEventListenerIntegrationTest {

    @MockitoBean
    private WhatsAppService whatsAppService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void shouldSendWelcomeMessageAfterTheNumberIsCommitted() {

        new TransactionTemplate(transactionManager).executeWithoutResult(status ->
                eventPublisher.publishEvent(new UserPhoneAddedEvent("Ahmed", "+966512345678")));

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() ->
                verify(whatsAppService).sendWhatsAppMessage(
                        eq("+966512345678"),
                        eq("ようこそ！ Welcome to Jisr Nihongo Platform, Ahmed! Your phone number has been updated. We are glad to have you with us."),
                        eq("phone-updated")));
    }

    @Test
    void shouldSendRegistrationWelcomeAfterTheAccountIsCommitted() {

        new TransactionTemplate(transactionManager).executeWithoutResult(status ->
                eventPublisher.publishEvent(new UserRegisteredEvent(
                        "Sara", "sara@example.com", "N5", "Conversational fluency", "+966512345678")));

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() ->
                verify(whatsAppService).sendWhatsAppMessage(
                        eq("+966512345678"),
                        eq("ようこそ！ Welcome to Jisr Nihongo Platform, Sara! Your account is now active. We are glad to have you with us."),
                        eq("registration-welcome")));
    }

    @Test
    void shouldNotSendRegistrationWelcomeBeforeTheTransactionCommits() {

        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            eventPublisher.publishEvent(new UserRegisteredEvent(
                    "Ghost", "ghost@example.com", "N5", "Nope", "+966500000000"));
            status.setRollbackOnly();
        });

        await().during(Duration.ofMillis(500)).atMost(Duration.ofSeconds(3)).untilAsserted(() ->
                verify(whatsAppService, never()).sendWhatsAppMessage(
                        eq("+966500000000"), any(), any()));
    }

    @Test
    void shouldNotDeliverBeforeTheTransactionCommits() {
        // rolled back, so no listener should ever run for it
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            eventPublisher.publishEvent(new UserPhoneAddedEvent("Ghost", "+966500000000"));
            status.setRollbackOnly();
        });

        await().during(Duration.ofMillis(500)).atMost(Duration.ofSeconds(3)).untilAsserted(() ->
                verify(whatsAppService, never()).sendWhatsAppMessage(any(), any(), any()));
    }

}
