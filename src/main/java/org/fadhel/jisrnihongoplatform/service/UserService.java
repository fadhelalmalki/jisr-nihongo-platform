package org.fadhel.jisrnihongoplatform.service;

import lombok.RequiredArgsConstructor;
import org.fadhel.jisrnihongoplatform.event.UserPhoneAddedEvent;
import org.fadhel.jisrnihongoplatform.event.UserRegisteredEvent;
import org.fadhel.jisrnihongoplatform.exception.ApiException;
import org.fadhel.jisrnihongoplatform.model.User;
import org.fadhel.jisrnihongoplatform.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AdminService adminService;
    private final ApplicationEventPublisher eventPublisher;

    // to get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // to get a user by id
    public User getUserById(Integer id) {

        User existing = userRepository.findUserById(id);
        if (existing == null) {
            throw new ApiException("User not found");
        }

        return existing;
    }

    // to add a user
    // a phone number is mandatory at signup because the account activation welcome goes over WhatsApp
    @Transactional
    public void addUser(User user) {

        String phone = user.getPhone() == null ? null : user.getPhone().trim();
        if (phone == null || phone.isEmpty()) {
            throw new ApiException("Phone number is required to register");
        }
        user.setPhone(phone);

        User savedUser = userRepository.save(user);

        eventPublisher.publishEvent(new UserRegisteredEvent(
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getJapaneseLevel(),
                savedUser.getLearningGoal(),
                savedUser.getPhone()));
    }

    // to update a user
    public void updateUser(Integer id, User user) {
        User existing = userRepository.findUserById(id);
        if (existing == null) {
            throw new ApiException("User not found");
        }
        existing.setName(user.getName());
        existing.setEmail(user.getEmail());
        existing.setPassword(user.getPassword());
        existing.setJapaneseLevel(user.getJapaneseLevel());
        existing.setLearningGoal(user.getLearningGoal());
        userRepository.save(existing);
    }

    // to delete a user
    public void deleteUser(Integer id) {
        User user = userRepository.findUserById(id);
        if (user == null) {
            throw new ApiException("User not found");
        }
        userRepository.delete(user);
    }

    // 15 outOf 15 to fetch all users by Japanese level (admin-only)
    public List<User> getUsersByLevel(String level, Integer requestingAdminId) {
        adminService.verifyAdmin(requestingAdminId);
        return userRepository.findUsersByJapaneseLevel(level);
    }

    // Extra Endpoint: 14 to notify after phone number change through WhatsApp
    @Transactional
    public boolean updateUserPhone(Integer id, String phone) {

        User existing = userRepository.findUserById(id);
        if (existing == null) {
            throw new ApiException("User not found");
        }

        String trimmed = phone == null ? null : phone.trim();
        String newPhone = (trimmed == null || trimmed.isEmpty()) ? null : trimmed;

        boolean changed = newPhone != null && !newPhone.equals(existing.getPhone());

        if (changed) {
            existing.setPhone(newPhone);
            userRepository.save(existing);
            eventPublisher.publishEvent(new UserPhoneAddedEvent(existing.getName(), newPhone));
        }

        return changed;
    }

}
