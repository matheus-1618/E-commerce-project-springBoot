package com.jtspringproject.JtSpringProject.services;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jtspringproject.JtSpringProject.dao.userDao;
import com.jtspringproject.JtSpringProject.models.PageResult;
import com.jtspringproject.JtSpringProject.models.PaginationRequest;
import com.jtspringproject.JtSpringProject.models.User;

@Service
public class userService {
	private static final Set<String> CUSTOMER_SORT_FIELDS = new LinkedHashSet<>(Arrays.asList("username", "email", "id"));
	private static final String DEFAULT_CUSTOMER_SORT = "username";

	private final userDao userDao;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public userService(userDao userDao, PasswordEncoder passwordEncoder) {
		this.userDao = userDao;
		this.passwordEncoder = passwordEncoder;
	}

	public List<User> getUsers() {
		return this.userDao.getAllUser();
	}

	public PageResult<User> getUsers(int page, int size, String sort) {
		PaginationRequest request = PaginationRequest.of(page, size, sort, CUSTOMER_SORT_FIELDS, DEFAULT_CUSTOMER_SORT);
		return this.userDao.getAllUser(request);
	}

	public User addUser(User user) {
		try {
			if (user.getPassword() != null && !isPasswordEncoded(user.getPassword())) {
				user.setPassword(passwordEncoder.encode(user.getPassword()));
			}
			return this.userDao.saveUser(user);
		} catch (DataIntegrityViolationException e) {
			throw new IllegalStateException("Unable to create user due to data integrity constraints.", e);
		}
	}

	public boolean checkUserExists(String username) {
		return this.userDao.userExists(username);
	}

	public User getUserByUsername(String username) {
		User user = userDao.getUserByUsername(username);
		if (user != null && user.getPassword() != null && !isPasswordEncoded(user.getPassword())) {
			// Migrate legacy plain-text passwords to BCrypt when the user is loaded.
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			userDao.saveUser(user);
		}
		return user;
	}

	public User getUserById(int id) {
		return this.userDao.getUserById(id);
	}

	public User updateUserProfile(int userId, String username, String email, String password, String address) {
		User existingUser = this.userDao.getUserById(userId);
		if (existingUser == null) {
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
