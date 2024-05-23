package ru.hse.server.repository;

import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.hse.database.dao.DaoGroup;
import ru.hse.database.entities.User;
import ru.hse.database.dao.DaoUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDatabaseRepository implements UserRepository {
    Logger logger = LoggerFactory.getLogger(UserRepository.class);

    @Override
    public User update(User user) {
        if (DaoUser.getUserById(user.getUserId()) == null) {
            return null;
        }

        DaoUser.createOrUpdateUser(user);
        return user;
    }

    @Override
    public User findByUserLogin(String login) {
        try {
            var user = DaoUser.getUserByLogin(login);
            logger.debug("find user={} by login={}", user, login);
            return user;
        } catch (DaoUser.NotUniqueUserLoginException e) {
            logger.error("find more than one user with the same login");
            return null;
        }
    }

    @Override
    public User findByUserEmail(String email) {
        try {
            var user = DaoUser.getUserByEmail(email);
            logger.debug("find user={} by email={}", user, email);
            return user;
        } catch (DaoUser.NotUniqueUserLoginException e) {
            logger.error("find more than one user with the same email");
            return null;
        }
    }

    @Override
    @Nonnull
    public <S extends User> S save(@Nonnull S entity) {
        DaoUser.createOrUpdateUser(entity);
        logger.info("user={} was created", entity);
        return entity;
    }

    @Override
    @Nonnull
    public <S extends User> Iterable<S> saveAll(@Nonnull Iterable<S> entities) {
        for (var user : entities) {
            DaoUser.createOrUpdateUser(user);
        }
        return entities;
    }

    @Override
    @Nonnull
    public Optional<User> findById(@Nonnull Long id) {
        var user = DaoUser.getUserById(id);
        if (user == null) {
            logger.debug("not found user with id={}", id);
            return Optional.empty();
        }

        logger.debug("found user={}, by id={}", user, id);

        return Optional.of(user);
    }

    @Override
    public boolean existsById(@Nonnull Long id) {
        var user = DaoUser.getUserById(id);
        if (user == null) {
            logger.debug("not found user with id={}", id);
            return false;
        }
        logger.debug("found user={} with id={}", user, id);
        return true;
    }

    @Override
    @Nonnull
    public Iterable<User> findAll() {
        logger.error("call unsupported operation deleteAll");
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    @Nonnull
    public Iterable<User> findAllById(@Nonnull Iterable<Long> ids) {
        List<User> users = new ArrayList<>();
        for (var id : ids) {
            var user = DaoUser.getUserById(id);
            if (user != null) {
                users.add(user);
            }
        }
        return users;
    }

    @Override
    public long count() {
        logger.error("call unsupported operation deleteAll");
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public void deleteById(@Nonnull Long id) {
        var user = DaoUser.getUserById(id);
        if (user != null) {
            delete(user);
        }
        logger.info("not found user with id={}, while try to delete him", id);
    }

    @Override
    public void delete(@Nonnull User entity) {
        logger.info("user={} was deleted", entity);
        DaoUser.deleteUser(entity);
    }

    @Override
    public void deleteAllById(@Nonnull Iterable<? extends Long> ids) {
        for (var id : ids) {
            deleteById(id);
        }
    }

    @Override
    public void deleteAll(@Nonnull Iterable<? extends User> entities) {
        for (var user : entities) {
            delete(user);
        }
    }

    @Override
    public void deleteAll() {
        logger.error("call unsupported operation deleteAll");
        throw new UnsupportedOperationException("not implemented yet");
    }
}
