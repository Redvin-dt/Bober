package ru.hse.server.repository;

import ru.hse.database.entities.User;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUserLogin(String login);
    User findByUserEmail(String email);
}
