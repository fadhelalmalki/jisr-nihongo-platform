package org.fadhel.jisrnihongoplatform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fadhel.jisrnihongoplatform.event.UserPhoneAddedEvent;
import org.fadhel.jisrnihongoplatform.event.UserRegisteredEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "app.whatsapp", name = "enabled", havingValue = "true")
public class WhatsAppEventListener {

    private final WhatsAppService whatsAppService;

    // to welcome the learner on WhatsApp once their new account is committed to the database
    // a registration without a number is skipped by the service guard
    @Async("whatsappTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onUserRegistered(UserRegisteredEvent event) {

        String message = "ようこそ！ Welcome to Jisr Nihongo Platform, " + event.userName()
                + "! Your account is now active. We are glad to have you with us.";

        whatsAppService.sendWhatsAppMessage(event.userPhone(), message, "registration-welcome");
    }

    // to confirm the new number on WhatsApp once it is committed to the database
    @Async("whatsappTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onUserPhoneAdded(UserPhoneAddedEvent event) {

        String message = "ようこそ！ Welcome to Jisr Nihongo Platform, " + event.userName()
                + "! Your phone number has been updated. We are glad to have you with us.";

        whatsAppService.sendWhatsAppMessage(event.userPhone(), message, "phone-updated");
    }

}
