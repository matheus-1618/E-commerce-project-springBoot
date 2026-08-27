package com.jtspringproject.JtSpringProject.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.jtspringproject.JtSpringProject.models.User;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("integrationuser");
        testUser.setEmail("integration@test.com");
        testUser.setPassword("$2a$10$encoded");
        testUser.setRole("ROLE_NORMAL");
        testUser.setAddress("Test Address");
        entityManager.persistAndFlush(testUser);
    }

    @Test
    void findByUsername_returnsUser() {
        Optional<User> found = userRepository.findByUsername("integrationuser");

        assertTrue(found.isPresent());
        assertEquals("integration@test.com", found.get().getEmail());
    }

    @Test
    void findByUsername_returnsEmptyForUnknown() {
        Optional<User> found = userRepository.findByUsername("nonexistent");

        assertFalse(found.isPresent());
    }

    @Test
    void existsByUsername_returnsTrueForExisting() {
        assertTrue(userRepository.existsByUsername("integrationuser"));
    }

    @Test
    void existsByUsername_returnsFalseForNonexistent() {
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }
}
