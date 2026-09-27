package org.fadhel.jisrnihongoplatform.event;

public record UserPhoneAddedEvent(
        String userName,
        String userPhone
) {
}
