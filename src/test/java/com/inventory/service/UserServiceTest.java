package com.inventory.service;

import com.inventory.entity.User;
import com.inventory.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void testFindByUserId() {
        User user = new User();
        user.setId(1L);
        user.setUserId("ADMIN001");
        user.setUsername("Administrator");

        when(userRepository.findByUserId("ADMIN001")).thenReturn(user);

        User result = userService.findByUserId("ADMIN001");
        assertNotNull(result);
        assertEquals("Administrator", result.getUsername());
    }

    @Test
    void testChangePasswordSuccess() {
        User user = new User();
        user.setUserId("USER001");
        user.setPassword("encodedOldPassword");

        when(userRepository.findByUserId("USER001")).thenReturn(user);
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        boolean success = userService.changePassword("USER001", "oldPassword", "newPassword");
        assertTrue(success);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testChangePasswordWrongCurrentPassword() {
        User user = new User();
        user.setUserId("USER001");
        user.setPassword("encodedOldPassword");

        when(userRepository.findByUserId("USER001")).thenReturn(user);
        when(passwordEncoder.matches("wrongPassword", "encodedOldPassword")).thenReturn(false);

        boolean success = userService.changePassword("USER001", "wrongPassword", "newPassword");
        assertFalse(success);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testSaveUserEncodesPassword() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("plainTextPassword");

        when(passwordEncoder.encode("plainTextPassword")).thenReturn("hashedPassword");
        when(userRepository.save(user)).thenReturn(user);

        User saved = userService.saveUser(user);
        assertEquals("hashedPassword", saved.getPassword());
        verify(passwordEncoder, times(1)).encode("plainTextPassword");
    }
}
