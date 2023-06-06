package com.portal.service;

import com.portal.repo.UserRepository;

import java.sql.SQLException;

public class UserService {
    public UserService() {
    }

    UserRepository userRepository = new UserRepository();

    public boolean login(String username, String password) {
        try {
            return userRepository.checkUser(username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
