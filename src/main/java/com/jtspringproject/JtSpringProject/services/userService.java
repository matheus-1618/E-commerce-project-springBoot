package com.jtspringproject.JtSpringProject.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jtspringproject.JtSpringProject.models.User;
import com.jtspringproject.JtSpringProject.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class userService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public userService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public List<User> getUsers() {
		return this.userRepository.findAll();
	}

	@Transactional
	public User addUser(User user) {
		try {
			if (user.getPassword() != null && !isPasswordEncoded(user.getPassword())) {
				user.setPassword(passwordEncoder.encode(user.getPassword()));
			}
			return this.userRepository.save(user);
		} catch (DataIntegrityViolationException e) {
			throw new IllegalStateException("Unable to create user due to data integrity constraints.", e);
		}
	}

	public boolean checkUserExists(String username) {
		return this.userRepository.existsByUsername(username);
	}

	@Transactional
	public User getUserByUsername(String username) {
		User user = userRepository.findByUsername(username).orElse(null);
		if (user != null && user.getPassword() != null && !isPasswordEncoded(user.getPassword())) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			userRepository.save(user);
		}
		return user;
	}

	public User getUserById(int id) {
		return this.userRepository.findById(id).orElse(null);
	}

	@Transactional
	public User updateUserProfile(int userId, String username, String email, String password, String address) {
		User existingUser = this.userRepository.findById(userId).orElse(null);
		if (existingUser == null) {
			return null;
		}

		existingUser.setUsername(username);
		existingUser.setEmail(email);
		existingUser.setAddress(address);

		if (password != null && !password.trim().isEmpty()) {
			existingUser.setPassword(isPasswordEncoded(password) ? password : passwordEncoder.encode(password));
		}

		return this.userRepository.save(existingUser);
	}

	private boolean isPasswordEncoded(String password) {
		return password != null
				&& (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"));
	}
}
