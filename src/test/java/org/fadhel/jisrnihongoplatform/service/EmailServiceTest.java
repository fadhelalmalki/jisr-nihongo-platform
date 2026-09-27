package org.fadhel.jisrnihongoplatform.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private EmailService emailService;

    @Test
    void shouldSendHtmlEmailWhenConfigured() throws MessagingException {

        ReflectionTestUtils.setField(emailService, "fromAddress", "no-reply@jisr-nihongo.test");
        ReflectionTestUtils.setField(emailService, "mailPassword", "test-app-password");

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("welcome-email"), any(Context.class))).thenReturn("<html>Welcome</html>");

        emailService.sendHtmlEmailWithLogo("learner@example.com", "Welcome to Jisr", "welcome-email",
                Map.of("userName", "Ahmed", "japaneseLevel", "N5", "learningGoal", "JLPT N4"));

        verify(mailSender).createMimeMessage();
        verify(templateEngine).process(eq("welcome-email"), any(Context.class));
        verify(mailSender).send(mimeMessage);
        verifyNoMoreInteractions(mailSender);
    }

    @Test
    void shouldSkipSendingWhenRecipientMissing() {

        ReflectionTestUtils.setField(emailService, "mailPassword", "test-app-password");

        emailService.sendHtmlEmailWithLogo(null, "Subject", "welcome-email", Map.of());
        emailService.sendHtmlEmailWithLogo("  ", "Subject", "welcome-email", Map.of());

        verifyNoInteractions(mailSender, templateEngine);
    }

    @Test
    void shouldSkipSendingWhenSenderMissing() {

        ReflectionTestUtils.setField(emailService, "fromAddress", "");
        ReflectionTestUtils.setField(emailService, "mailPassword", "test-app-password");

        emailService.sendHtmlEmailWithLogo("learner@example.com", "Subject", "welcome-email", Map.of("userName", "Ahmed"));

        verifyNoInteractions(mailSender, templateEngine);
    }

    @Test
    void shouldSkipSendingWhenMailPasswordMissing() {

        ReflectionTestUtils.setField(emailService, "mailPassword", "");

        emailService.sendHtmlEmailWithLogo("learner@example.com", "Subject", "welcome-email", Map.of("userName", "Ahmed"));

        verifyNoInteractions(mailSender, templateEngine);
    }

    @Test
    void shouldSwallowMailExceptionAndNotPropagate() throws MessagingException {

        ReflectionTestUtils.setField(emailService, "fromAddress", "no-reply@jisr-nihongo.test");
        ReflectionTestUtils.setField(emailService, "mailPassword", "test-app-password");

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("certificate-email"), any(Context.class))).thenReturn("<html>Cert</html>");
        doThrow(new RuntimeException("SMTP timeout")).when(mailSender).send(any(MimeMessage.class));

        emailService.sendHtmlEmailWithLogo("learner@example.com", "Your Certificate", "certificate-email",
                Map.of("userName", "Ahmed", "courseTitle", "N5", "certificateNumber", "JISR-CERT-ABC123"));

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void shouldProcessTemplateWithProvidedVariables() throws MessagingException {

        ReflectionTestUtils.setField(emailService, "fromAddress", "no-reply@jisr-nihongo.test");
        ReflectionTestUtils.setField(emailService, "mailPassword", "test-app-password");

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("enrollment-email"), any(Context.class))).thenReturn("<html>Enrollment</html>");

        Map<String, Object> variables = Map.of("userName", "Fatima", "courseTitle", "Genki I", "courseLevel", "N5", "instructorName", "Sato Sensei");
        emailService.sendHtmlEmailWithLogo("fatima@example.com", "Enrollment Confirmed", "enrollment-email", variables);

        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq("enrollment-email"), contextCaptor.capture());

        Context captured = contextCaptor.getValue();
        assertThat(captured.getVariable("userName")).isEqualTo("Fatima");
        assertThat(captured.getVariable("courseTitle")).isEqualTo("Genki I");
        assertThat(captured.getVariable("courseLevel")).isEqualTo("N5");
        assertThat(captured.getVariable("instructorName")).isEqualTo("Sato Sensei");
    }
}
