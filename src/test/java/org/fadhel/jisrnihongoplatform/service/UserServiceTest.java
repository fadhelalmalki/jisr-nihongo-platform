package org.fadhel.jisrnihongoplatform.service;

import org.fadhel.jisrnihongoplatform.event.UserPhoneAddedEvent;
import org.fadhel.jisrnihongoplatform.event.UserRegisteredEvent;
import org.fadhel.jisrnihongoplatform.exception.ApiException;
import org.fadhel.jisrnihongoplatform.model.User;
import org.fadhel.jisrnihongoplatform.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdminService adminService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldPublishPhoneAddedEventWhenNumberIsNew() {

        User user = existingUser(null);
        when(userRepository.findUserById(7)).thenReturn(user);

        boolean changed = userService.updateUserPhone(7, "+966512345678");

        assertThat(changed).isTrue();
        assertThat(user.getPhone()).isEqualTo("+966512345678");
        verify(userRepository).save(user);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isEqualTo(new UserPhoneAddedEvent("Ahmed", "+966512345678"));
    }

    @Test
    void shouldNotPublishEventWhenNumberUnchanged() {
        // the ban-risk guard: re-saving the same number must never re-announce it
        User user = existingUser("+966512345678");
        when(userRepository.findUserById(7)).thenReturn(user);

        boolean changed = userService.updateUserPhone(7, "+966512345678");

        assertThat(changed).isFalse();
        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any(Object.class));
    }

    @Test
    void shouldPublishEventWhenNumberReplacedWithADifferentOne() {

        User user = existingUser("+966512345678");
        when(userRepository.findUserById(7)).thenReturn(user);

        boolean changed = userService.updateUserPhone(7, "+966599999999");

        assertThat(changed).isTrue();
        assertThat(user.getPhone()).isEqualTo("+966599999999");

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isEqualTo(new UserPhoneAddedEvent("Ahmed", "+966599999999"));
    }

    @Test
    void shouldNotPublishEventWhenPhoneIsBlank() {

        User user = existingUser("+966512345678");
        when(userRepository.findUserById(7)).thenReturn(user);

        boolean changed = userService.updateUserPhone(7, "   ");

        assertThat(changed).isFalse();
        assertThat(user.getPhone()).isEqualTo("+966512345678");
        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any(Object.class));
    }

    @Test
    void shouldTrimSurroundingWhitespaceFromPhone() {

        User user = existingUser(null);
        when(userRepository.findUserById(7)).thenReturn(user);

        boolean changed = userService.updateUserPhone(7, "  +966512345678  ");

        assertThat(changed).isTrue();
        assertThat(user.getPhone()).isEqualTo("+966512345678");
    }

    @Test
    void shouldThrowWhenUserNotFound() {

        when(userRepository.findUserById(99)).thenReturn(null);

        assertThatThrownBy(() -> userService.updateUserPhone(99, "+966512345678"))
                .isInstanceOf(ApiException.class)
                .hasMessage("User not found");

        verify(eventPublisher, never()).publishEvent(any(Object.class));
    }

    @Test
    void shouldForwardThePhoneIntoTheRegistrationEvent() {

        User user = new User();
        user.setName("Sara");
        user.setEmail("sara@example.com");
        user.setPassword("secret123");
        user.setJapaneseLevel("N5");
        user.setLearningGoal("Conversational fluency");
        user.setPhone("+966512345678");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.addUser(user);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isEqualTo(
                new UserRegisteredEvent("Sara", "sara@example.com", "N5", "Conversational fluency", "+966512345678"));
    }

    @Test
    void shouldTrimWhitespaceFromTheRegistrationPhone() {

        User user = new User();
        user.setName("Sara");
        user.setEmail("sara@example.com");
        user.setPassword("secret123");
        user.setJapaneseLevel("N5");
        user.setLearningGoal("Conversational fluency");
        user.setPhone("  +966512345678  ");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.addUser(user);

        assertThat(user.getPhone()).isEqualTo("+966512345678");
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(((UserRegisteredEvent) eventCaptor.getValue()).userPhone()).isEqualTo("+966512345678");
    }

    @Test
    void shouldRejectRegistrationWithoutAPhone() {

        User user = new User();
        user.setName("Sara");
        user.setEmail("sara@example.com");
        user.setPassword("secret123");
        user.setJapaneseLevel("N5");
        user.setLearningGoal("Conversational fluency");

        assertThatThrownBy(() -> userService.addUser(user))
                .isInstanceOf(ApiException.class)
                .hasMessage("Phone number is required to register");

        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any(Object.class));
    }

    @Test
    void shouldRejectRegistrationWithWhitespaceOnlyPhone() {

        User user = new User();
        user.setName("Sara");
        user.setEmail("sara@example.com");
        user.setPassword("secret123");
        user.setJapaneseLevel("N5");
        user.setLearningGoal("Conversational fluency");
        user.setPhone("   ");

        assertThatThrownBy(() -> userService.addUser(user))
                .isInstanceOf(ApiException.class)
                .hasMessage("Phone number is required to register");

        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any(Object.class));
    }

    private User existingUser(String phone) {
        User user = new User();
        user.setId(7);
        user.setName("Ahmed");
        user.setEmail("ahmed@example.com");
        user.setPassword("secret123");
        user.setJapaneseLevel("N5");
        user.setLearningGoal("Pass JLPT N4");
        user.setPhone(phone);
        return user;
    }

}
