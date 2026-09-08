package org.example.javalogin.repository;

import org.example.javalogin.entity.User;

import javax.persistence.EntityManager;

public class UserRepository {

    private final EntityManager entityManager;

    public UserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public User findByUsername(String username) {
        try {
            return entityManager
                    .createQuery(
                            "SELECT u FROM User u WHERE u.username = :username",
                            User.class
                    )
                    .setParameter("username", username)
                    .getSingleResult();

        } catch (Exception e) {
            return null;
        }
    }

    public void save(User user) {
        entityManager.persist(user);
    }
}