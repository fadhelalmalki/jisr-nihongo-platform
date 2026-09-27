package org.fadhel.jisrnihongoplatform.service;

import lombok.RequiredArgsConstructor;
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
    @Transactional
    public void addUser(User user) {

        User savedUser = userRepository.save(user);

        eventPublisher.publishEvent(new UserRegisteredEvent(
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getJapaneseLevel(),
                savedUser.getLearningGoal()));
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

}
