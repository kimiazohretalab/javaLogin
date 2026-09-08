package org.example.javalogin.service;

import org.example.javalogin.entity.User;
import org.example.javalogin.repository.UserRepository;
import org.example.javalogin.util.PasswordUtil;

import javax.persistence.EntityManager;

public class UserService {

    private final UserRepository userRepository;

    public UserService(EntityManager entityManager) {
        this.userRepository =
                new UserRepository(entityManager);
    }

    public boolean login(
            String username,
            String password
    ) {

        User user =
                userRepository.findByUsername(username);

        if (user == null) {
            return false;
        }

        return PasswordUtil.checkPassword(
                password,
                user.getPassword()
        );
    }

    public boolean register(
            String username,
            String password
    ) {

        User existingUser =
                userRepository.findByUsername(username);

        if (existingUser != null) {
            return false;
        }

        String hashedPassword =
                PasswordUtil.hashPassword(password);

        User user =
                new User(username, hashedPassword);

        userRepository.save(user);

        return true;
    }
}