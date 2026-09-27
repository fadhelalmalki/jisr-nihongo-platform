package org.fadhel.jisrnihongoplatform.service;

import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.Part;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Renders every email template through the real Thymeleaf engine and assembles a real
 * MimeMessage, so broken templates or MIME problems surface here instead of at runtime.
 * The JavaMailSender is mocked, so no SMTP connection is ever opened.
 */
@SpringBootTest
class EmailTemplateIntegrationTest {

    @MockitoBean
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromAddress", "no-reply@jisr-nihongo.test");
        ReflectionTestUtils.setField(emailService, "mailPassword", "test-app-password");
    }

    @Test
    void shouldRenderWelcomeTemplate() throws Exception {

        Map<String, Object> variables = new HashMap<>();
        variables.put("userName", "Ahmed");
        variables.put("japaneseLevel", "N5");
        variables.put("learningGoal", "Pass JLPT N4");

        MimeMessage message = send("learner@example.com", "Welcome to Jisr", "welcome-email", variables);

        assertThat(message.getSubject()).isEqualTo("Welcome to Jisr");
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo("learner@example.com");
        assertThat(message.getFrom()[0].toString()).isEqualTo("no-reply@jisr-nihongo.test");
        assertThat(textOf(message)).contains("Ahmed").contains("N5").contains("Pass JLPT N4");
    }

    @Test
    void shouldRenderEnrollmentTemplate() throws Exception {

        Map<String, Object> variables = new HashMap<>();
        variables.put("userName", "Fatima");
        variables.put("courseTitle", "Genki I");
        variables.put("courseLevel", "N5");
        variables.put("instructorName", "Sato Sensei");

        MimeMessage message = send("fatima@example.com", "Enrollment Confirmed: Genki I", "enrollment-email", variables);

        assertThat(message.getSubject()).isEqualTo("Enrollment Confirmed: Genki I");
        assertThat(textOf(message)).contains("Fatima").contains("Genki I").contains("N5").contains("Sato Sensei");
    }

    @Test
    void shouldRenderCertificateTemplate() throws Exception {

        Map<String, Object> variables = new HashMap<>();
        variables.put("userName", "Omar");
        variables.put("courseTitle", "Genki II");
        variables.put("certificateNumber", "JISR-CERT-ABC123");
        variables.put("issuedAt", "2026-09-27 14:30");

        MimeMessage message = send("omar@example.com", "Your Certificate: JISR-CERT-ABC123", "certificate-email", variables);

        assertThat(message.getSubject()).isEqualTo("Your Certificate: JISR-CERT-ABC123");
        assertThat(textOf(message))
                .contains("Omar")
                .contains("Genki II")
                .contains("JISR-CERT-ABC123")
                .contains("2026-09-27 14:30");
    }

    @Test
    void shouldInlineLogoUnderTheCidReferenceUsedByTheTemplates() throws Exception {

        MimeMessage message = send("learner@example.com", "Subject", "welcome-email", Map.of("userName", "Ahmed"));

        assertThat(textOf(message)).contains("cid:jisrLogo");
        Part logo = findPartByContentId(message, "jisrLogo");
        assertThat(logo).isNotNull();
        assertThat(logo.getContentType()).startsWith("image/");
    }

    private MimeMessage send(String to, String subject, String template, Map<String, Object> variables) throws Exception {

        MimeMessage mimeMessage = new JavaMailSenderImpl().createMimeMessage();
        Mockito.when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendHtmlEmailWithLogo(to, subject, template, variables);

        verify(mailSender).send(mimeMessage);

        // to finalise the Content-Type and boundary headers, which the real sender does during delivery
        mimeMessage.saveChanges();
        return mimeMessage;
    }

    // to walk the nested multipart structure and return the rendered email body
    private String textOf(MimeMessage message) throws Exception {
        return firstMatching(message, part -> {
            try {
                return part.isMimeType("text/*");
            } catch (MessagingException e) {
                return false;
            }
        });
    }

    private Part findPartByContentId(MimeMessage message, String contentId) throws Exception {
        return firstMatchingPart(message, part -> {
            if (!(part instanceof MimeBodyPart mimeBodyPart)) {
                return false;
            }
            try {
                String id = mimeBodyPart.getContentID();
                return contentId.equals(id) || ("<" + contentId + ">").equals(id);
            } catch (MessagingException e) {
                return false;
            }
        });
    }

    private String firstMatching(Part part, java.util.function.Predicate<Part> predicate) throws Exception {
        Part found = firstMatchingPart(part, predicate);
        assertThat(found).as("no matching body part found").isNotNull();
        return String.valueOf(found.getContent());
    }

    private Part firstMatchingPart(Part part, java.util.function.Predicate<Part> predicate) throws Exception {

        if (predicate.test(part)) {
            return part;
        }

        if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                Part found = firstMatchingPart(bodyPart, predicate);
                if (found != null) {
                    return found;
                }
            }
        }

        return null;
    }
}
