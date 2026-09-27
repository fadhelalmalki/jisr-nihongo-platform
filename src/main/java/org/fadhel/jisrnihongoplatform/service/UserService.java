package org.fadhel.jisrnihongoplatform.service;

import lombok.RequiredArgsConstructor;
import org.fadhel.jisrnihongoplatform.exception.ApiException;
import org.fadhel.jisrnihongoplatform.model.User;
import org.fadhel.jisrnihongoplatform.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AdminService adminService;
    private final EmailService emailService;

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
    public void addUser(User user) {
        userRepository.save(user);

//        // Send Welcome Email
//        Map<String, Object> variables = Map.of(
//                "userName", user.getName(),
//                "japaneseLevel", user.getJapaneseLevel(),
//                "learningGoal", user.getLearningGoal()
//        );
//
//        emailService.sendHtmlEmailWithLogo(
//                user.getEmail(),
//                "ようこそ！ Welcome to Jisr Nihongo Platform",
//                "welcome-email",
//                variables
//        );
        Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("userName", user.getName() != null ? user.getName() : "Student");
        variables.put("japaneseLevel", user.getJapaneseLevel() != null ? user.getJapaneseLevel() : "N5");
        variables.put("learningGoal", user.getLearningGoal() != null ? user.getLearningGoal() : "Learn Japanese");

        emailService.sendHtmlEmailWithLogo(
                user.getEmail(),
                "ようこそ！ Welcome to Jisr Nihongo Platform",
                "welcome-email",
                variables
        );
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
