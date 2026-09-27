package org.fadhel.jisrnihongoplatform.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.from:${spring.mail.username:}}")
    private String fromAddress;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    // to render a Thymeleaf template and deliver it as an HTML email with the platform logo inlined
    // invoked asynchronously by EmailEventListener, so this method itself stays synchronous
    public void sendHtmlEmailWithLogo(String to, String subject, String templateName, Map<String, Object> templateVariables) {

        if (to == null || to.isBlank()) {
            log.warn("Skipping email '{}' because the recipient address is missing", subject);
            return;
        }

        if (fromAddress == null || fromAddress.isBlank()) {
            log.warn("Skipping email to {} because no sender address is configured (set MAIL_USERNAME)", to);
            return;
        }

        if (mailPassword == null || mailPassword.isBlank()) {
            log.warn("Skipping email to {} because no SMTP password is configured (set MAIL_PASSWORD)", to);
            return;
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariables(templateVariables);

            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(templateEngine.process(templateName, context), true);

            attachLogo(helper);

            mailSender.send(mimeMessage);
            log.info("Email '{}' sent to {}", subject, to);

        } catch (Exception e) {
            // to never let a delivery problem roll back or fail the business operation that triggered it
            log.error("Failed to send email '{}' to {}: {}", subject, to, e.getMessage(), e);
        }
    }

    // to attach the platform logo as a cid reference so email clients render it without a public URL
    private void attachLogo(MimeMessageHelper helper) {
        ClassPathResource logo = new ClassPathResource("static/images/logo.jpg");
        if (!logo.exists()) {
            log.warn("Logo not found on the classpath, sending the email without it");
            return;
        }
        try {
            helper.addInline("jisrLogo", logo);
        } catch (Exception e) {
            log.warn("Could not inline the logo into the email: {}", e.getMessage());
        }
    }

}
