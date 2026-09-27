package org.fadhel.jisrnihongoplatform.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.regex.Pattern;

@Service
@Slf4j
public class WhatsAppService {

    // to reject anything the gateway would not accept, before it is charged for or silently dropped
    private static final Pattern E164 = Pattern.compile("^\\+[1-9]\\d{7,14}$");

    @Value("${app.whatsapp.base-url:https://api.ultramsg.com}")
    private String baseUrl;

    @Value("${app.whatsapp.instance-id:}")
    private String instanceId;

    @Value("${app.whatsapp.token:}")
    private String apiToken;

    private final RestClient restClient;

    public WhatsAppService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    // to deliver a plain text message through the UltraMsg gateway
    // invoked asynchronously by WhatsAppEventListener, so this method itself stays synchronous
    public void sendWhatsAppMessage(String to, String message, String referenceId) {

        if (to == null || to.isBlank()) {
            log.warn("Skipping WhatsApp message because the recipient number is missing");
            return;
        }

        if (!E164.matcher(to).matches()) {
            log.warn("Skipping WhatsApp message to {} because the number is not in E.164 format", to);
            return;
        }

        if (instanceId == null || instanceId.isBlank()) {
            log.warn("Skipping WhatsApp message to {} because no gateway instance is configured (set WHATSAPP_INSTANCE_ID)", to);
            return;
        }

        if (apiToken == null || apiToken.isBlank()) {
            log.warn("Skipping WhatsApp message to {} because no gateway token is configured (set WHATSAPP_TOKEN)", to);
            return;
        }

        try {
            // the gateway documents the recipient with a leading plus but only accepts bare digits
            String recipient = to.replaceAll("[^0-9]", "");

            // the gateway expects application/x-www-form-urlencoded, not JSON
            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("token", apiToken);
            form.add("to", recipient);
            form.add("body", message);
            form.add("referenceId", referenceId);

            String response = restClient.post()
                    .uri(baseUrl + "/" + instanceId + "/messages/chat")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(String.class);

            log.info("WhatsApp message sent to {}: {}", to, response);

        } catch (Exception e) {
            // to never let a gateway problem roll back or fail the operation that triggered it
            log.error("Failed to send WhatsApp message to {}: {}", to, e.getMessage(), e);
        }
    }

}
