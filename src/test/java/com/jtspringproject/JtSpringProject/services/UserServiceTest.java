package com.jtspringproject.JtSpringProject.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.jtspringproject.JtSpringProject.models.User;
import com.jtspringproject.JtSpringProject.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private userService service;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("plaintext");
        testUser.setRole("ROLE_NORMAL");
        testUser.setAddress("123 Street");
    }

    @Test
    void getUsers_returnsAllUsers() {
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = service.getUsers();

        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
    }

    @Test
    void addUser_encodesPlaintextPassword() {
        when(passwordEncoder.encode("plaintext")).thenReturn("$2a$10$encoded");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        service.addUser(testUser);

        verify(passwordEncoder).encode("plaintext");
        verify(userRepository).save(testUser);
    }

    @Test
    void addUser_skipsEncodingForBcryptPassword() {
        testUser.setPassword("$2a$10$alreadyEncoded");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        service.addUser(testUser);

        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void addUser_throwsOnDataIntegrityViolation() {
        when(passwordEncoder.encode(any())).thenReturn("$2a$10$encoded");
        when(userRepository.save(any())).thenThrow(new DataIntegrityViolationException("duplicate"));

        assertThrows(IllegalStateException.class, () -> service.addUser(testUser));
    }

    @Test
    void checkUserExists_returnsTrue() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertTrue(service.checkUserExists("testuser"));
    }

    @Test
    void checkUserExists_returnsFalse() {
        when(userRepository.existsByUsername("unknown")).thenReturn(false);

        assertFalse(service.checkUserExists("unknown"));
    }

    @Test
    void getUserByUsername_returnsUser() {
        testUser.setPassword("$2a$10$alreadyEncoded");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        User result = service.getUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void getUserByUsername_migratesPlaintextPassword() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("plaintext")).thenReturn("$2a$10$migrated");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        service.getUserByUsername("testuser");

        verify(passwordEncoder).encode("plaintext");
        verify(userRepository).save(testUser);
    }

    @Test
    void getUserByUsername_returnsNullWhenNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertNull(service.getUserByUsername("unknown"));
    }

    @Test
    void getUserById_returnsUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        User result = service.getUserById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void getUserById_returnsNullWhenNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertNull(service.getUserById(99));
    }

    @Test
    void updateUserProfile_updatesAllFields() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newpass")).thenReturn("$2a$10$newEncoded");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = service.updateUserProfile(1, "newname", "new@email.com", "newpass", "456 Ave");

        assertNotNull(result);
        verify(userRepository).save(testUser);
    }

    @Test
    void updateUserProfile_returnsNullWhenUserNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertNull(service.updateUserProfile(99, "name", "email", "pass", "addr"));
    }

    @Test
    void updateUserProfile_skipsPasswordWhenBlank() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        service.updateUserProfile(1, "newname", "new@email.com", "", "456 Ave");

        verify(passwordEncoder, never()).encode(any());
    }
}
