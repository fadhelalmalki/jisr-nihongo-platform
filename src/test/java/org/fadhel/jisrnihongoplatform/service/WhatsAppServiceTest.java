package org.fadhel.jisrnihongoplatform.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class WhatsAppServiceTest {

    private MockRestServiceServer server;
    private WhatsAppService whatsAppService;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        whatsAppService = new WhatsAppService(builder);
        ReflectionTestUtils.setField(whatsAppService, "baseUrl", "https://api.ultramsg.com");
        ReflectionTestUtils.setField(whatsAppService, "instanceId", "instance123");
        ReflectionTestUtils.setField(whatsAppService, "apiToken", "test-token");
    }

    @Test
    void shouldPostFormEncodedMessageToTheGateway() {
        server.expect(once(), requestTo("https://api.ultramsg.com/instance123/messages/chat"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(content().formData(form(
                        "token", "test-token",
                        "to", "966512345678",
                        "body", "Welcome aboard",
                        "referenceId", "phone-welcome")))
                .andRespond(withSuccess("{\"id\":\"MSG-1\"}", MediaType.APPLICATION_JSON));

        whatsAppService.sendWhatsAppMessage("+966512345678", "Welcome aboard", "phone-welcome");

        server.verify();
    }

    @Test
    void shouldSendRecipientAsBareDigitsWithoutTheLeadingPlus() {
        // the gateway documents the number with a plus but only accepts bare digits,
        // so assert on the raw wire body that the plus never reaches it
        server.expect(once(), requestTo("https://api.ultramsg.com/instance123/messages/chat"))
                .andExpect(content().string(containsString("to=966512345678")))
                .andExpect(content().string(not(containsString("+966"))))
                .andRespond(withSuccess("{\"id\":\"MSG-1\"}", MediaType.APPLICATION_JSON));

        whatsAppService.sendWhatsAppMessage("+966512345678", "Welcome aboard", "phone-welcome");

        server.verify();
    }

    @Test
    void shouldSkipWhenRecipientMissing() {
        // no expectation is registered, so any outbound request fails the test
        whatsAppService.sendWhatsAppMessage(null, "Welcome aboard", "phone-welcome");
        whatsAppService.sendWhatsAppMessage("   ", "Welcome aboard", "phone-welcome");

        server.verify();
    }

    @Test
    void shouldSkipWhenRecipientNotE164() {
        whatsAppService.sendWhatsAppMessage("966512345678", "Welcome aboard", "phone-welcome");
        whatsAppService.sendWhatsAppMessage("+966 512 345 678", "Welcome aboard", "phone-welcome");
        whatsAppService.sendWhatsAppMessage("00966512345678", "Welcome aboard", "phone-welcome");

        server.verify();
    }

    @Test
    void shouldSkipWhenInstanceIdMissing() {
        ReflectionTestUtils.setField(whatsAppService, "instanceId", "");

        whatsAppService.sendWhatsAppMessage("+966512345678", "Welcome aboard", "phone-welcome");

        server.verify();
    }

    @Test
    void shouldSkipWhenTokenMissing() {
        ReflectionTestUtils.setField(whatsAppService, "apiToken", "  ");

        whatsAppService.sendWhatsAppMessage("+966512345678", "Welcome aboard", "phone-welcome");

        server.verify();
    }

    @Test
    void shouldSwallowGatewayErrorAndNotPropagate() {
        server.expect(once(), requestTo("https://api.ultramsg.com/instance123/messages/chat"))
                .andRespond(withServerError());

        assertThatCode(() ->
                whatsAppService.sendWhatsAppMessage("+966512345678", "Welcome aboard", "phone-welcome"))
                .doesNotThrowAnyException();

        server.verify();
    }

    // to build the expected form body from flat key value pairs
    private MultiValueMap<String, String> form(String... keyValues) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            form.add(keyValues[i], keyValues[i + 1]);
        }
        return form;
    }

}
