package com.inventory.service;

import com.inventory.entity.User;
import com.inventory.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean changePassword(String userId, String currentPassword, String newPassword) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            return false;
        }

        if (currentPassword == null || newPassword == null || newPassword.isBlank()) {
            return false;
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;
        }

        user.setPassword(newPassword);
        saveUser(user);
        return true;
    }

    public User saveUser(User user) {
        if (user.getPassword() != null && !user.getPassword().startsWith("{")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public User updateUserProfile(User updatedUser, String newPassword) {
        User existingUser = userRepository.findById(updatedUser.getId()).orElseThrow();
        existingUser.setUserId(updatedUser.getUserId() == null ? existingUser.getUserId() : updatedUser.getUserId().trim());
        existingUser.setUsername(updatedUser.getUsername() == null ? "" : updatedUser.getUsername().trim());
        existingUser.setRole(updatedUser.getRole());
        existingUser.setPhone(updatedUser.getPhone() == null ? "" : updatedUser.getPhone().trim());
        existingUser.setEmail(updatedUser.getEmail() == null ? "" : updatedUser.getEmail().trim());
        existingUser.setAddress(updatedUser.getAddress() == null ? "" : updatedUser.getAddress().trim());

        if (newPassword != null && !newPassword.isBlank()) {
            existingUser.setPassword(newPassword);
        }

        return saveUser(existingUser);
    }

    public boolean userIdExists(String userId) {
        return userRepository.existsByUserId(userId);
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean userIdExistsForAnotherUser(String userId, Long id) {
        return userRepository.existsByUserIdAndIdNot(userId, id);
    }

    public boolean usernameExistsForAnotherUser(String username, Long id) {
        return userRepository.existsByUsernameAndIdNot(username, id);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public long getUserCount() {
        return userRepository.count();
    }
}
