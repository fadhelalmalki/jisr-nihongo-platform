package org.fadhel.jisrnihongoplatform.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String senderEmail;

    /**
     * Renders Thymeleaf HTML template, attaches the embedded Jisr Logo via CID, and sends email.
     */
    public void sendHtmlEmailWithLogo(String to, String subject, String templateName, Map<String, Object> templateVariables) {
        try {
            Context context = new Context();
            context.setVariables(templateVariables);

            String htmlContent = templateEngine.process(templateName, context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            // Check for logo.png first, then logo.jpg
            ClassPathResource logoResource = new ClassPathResource("static/images/logo.png");
            if (!logoResource.exists()) {
                logoResource = new ClassPathResource("static/images/logo.jpg");
            }

            if (logoResource.exists()) {
                helper.addInline("jisrLogo", logoResource);
            }

            mailSender.send(mimeMessage);
            System.out.println("Email successfully sent to: " + to);

//            // Embed Logo image via Content-ID (CID)
//            ClassPathResource logoResource = new ClassPathResource("static/images/logo.jpg");
//            if (logoResource.exists()) {
//                helper.addInline("jisrLogo", logoResource);
//            }

            mailSender.send(mimeMessage);
        } catch (Exception e) { // Catches RuntimeExceptions, MailExceptions, and Thymeleaf errors
            System.err.println("Email notification failed: " + e.getMessage());
            e.printStackTrace(); // Prints exact SMTP failure reason in IntelliJ console
        }
//        catch (MessagingException e) {
//            System.err.println("Email notification delivery failed: " + e.getMessage());
//        }
    }

}
