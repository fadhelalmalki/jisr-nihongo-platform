package org.fadhel.jisrnihongoplatform.event;

public record UserRegisteredEvent(
        String userName,
        String userEmail,
        String japaneseLevel,
        String learningGoal
) {
}
