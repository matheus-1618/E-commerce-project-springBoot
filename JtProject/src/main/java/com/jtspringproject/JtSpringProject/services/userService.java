package com.jtspringproject.JtSpringProject.services;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jtspringproject.JtSpringProject.dao.userDao;
import com.jtspringproject.JtSpringProject.models.User;

@Service
public class userService {
	@Autowired
	private userDao userDao;

	public List<User> getUsers() {
		return this.userDao.getAllUser();
	}

	public User addUser(User user) {
		String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10));
		user.setPassword(hashedPassword);
		return this.userDao.saveUser(user);
	}

	public User checkLogin(String username, String password) {
		User user = this.userDao.getUserByUsername(username);
		if (user == null) {
			return new User();
		}
		if (BCrypt.checkpw(password, user.getPassword())) {
			return user;
		}
		return new User();
	}
}
