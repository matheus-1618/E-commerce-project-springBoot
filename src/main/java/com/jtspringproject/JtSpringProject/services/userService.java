package com.jtspringproject.JtSpringProject.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jtspringproject.JtSpringProject.dao.userDao;
import com.jtspringproject.JtSpringProject.models.User;

@Service
public class userService {
	private static final Logger logger = LoggerFactory.getLogger(userService.class);

	private final userDao userDao;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public userService(userDao userDao, PasswordEncoder passwordEncoder) {
		this.userDao = userDao;
		this.passwordEncoder = passwordEncoder;
	}

	public List<User> getUsers() {
		logger.debug("Fetching all users");
		return this.userDao.getAllUser();
	}

	public User addUser(User user) {
		logger.info("Registering new user username={}", user.getUsername());
		try {
			if (user.getPassword() != null && !isPasswordEncoded(user.getPassword())) {
				user.setPassword(passwordEncoder.encode(user.getPassword()));
			}
			return this.userDao.saveUser(user);
		} catch (DataIntegrityViolationException e) {
			logger.warn("User registration failed for username={}", user.getUsername(), e);
			throw new IllegalStateException("Unable to create user due to data integrity constraints.", e);
		}
	}

	public boolean checkUserExists(String username) {
		logger.debug("Checking user existence username={}", username);
		return this.userDao.userExists(username);
	}

	public User getUserByUsername(String username) {
		logger.debug("Loading user by username={}", username);
		User user = userDao.getUserByUsername(username);
		if (user != null && user.getPassword() != null && !isPasswordEncoded(user.getPassword())) {
			logger.info("Migrating legacy password for username={}", username);
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			userDao.saveUser(user);
		}
		return user;
	}

	public User getUserById(int id) {
		logger.debug("Fetching user by id={}", id);
		return this.userDao.getUserById(id);
	}

	public User updateUserProfile(int userId, String username, String email, String password, String address) {
		logger.info("Updating profile for user id={}", userId);
		User existingUser = this.userDao.getUserById(userId);
		if (existingUser == null) {
			logger.warn("User id={} not found for profile update", userId);
			return null;
		}

		existingUser.setUsername(username);
		existingUser.setEmail(email);
		existingUser.setAddress(address);

		if (password != null && !password.trim().isEmpty()) {
			existingUser.setPassword(isPasswordEncoded(password) ? password : passwordEncoder.encode(password));
		}

		return this.userDao.saveUser(existingUser);
	}

	private boolean isPasswordEncoded(String password) {
		return password != null
				&& (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"));
	}
}
