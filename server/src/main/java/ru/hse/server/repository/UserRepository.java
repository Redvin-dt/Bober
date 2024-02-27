package ru.hse.server.repository;

import ru.hse.server.entity.UserEntity;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    UserEntity findByLogin(String login);
}
