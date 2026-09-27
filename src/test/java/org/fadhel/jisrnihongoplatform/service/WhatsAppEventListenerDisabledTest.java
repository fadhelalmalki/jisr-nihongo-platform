package org.fadhel.jisrnihongoplatform.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The WhatsApp channel must stay completely absent unless it is explicitly enabled,
 * so the application boots normally on a machine that has no gateway instance.
 */
@SpringBootTest(properties = "app.whatsapp.enabled=false")
class WhatsAppEventListenerDisabledTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void shouldNotRegisterTheListenerWhenDisabled() {
        assertThat(context.getBeansOfType(WhatsAppEventListener.class)).isEmpty();
    }

}
