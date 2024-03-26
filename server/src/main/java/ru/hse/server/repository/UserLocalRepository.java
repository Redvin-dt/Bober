package ru.hse.server.repository;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Repository;
import ru.hse.database.entities.User;

import java.util.*;

/**
 * class only for test
 */
@Repository
public class UserLocalRepository implements UserRepository {

    Map<Long, User> storage = new TreeMap<>();
    private long counter = 1;

    @Override
    @Nonnull
    public <S extends User> S save(@Nonnull S entity) {
        storage.put(counter++, entity);
        return entity;
    }

    @Override
    @Nonnull
    public <S extends User> Iterable<S> saveAll(Iterable<S> entities) {
        List<S> list = new ArrayList<>();
        for (var entity : entities) {
            list.add(save(entity));
        }
        return list;
    }

    @Override
    @Nonnull
    public Optional<User> findById(@Nonnull Long aLong) {
        if (storage.containsKey(aLong)) {
            return Optional.of(storage.get(aLong));
        }
        return Optional.empty();
    }

    @Override
    public boolean existsById(@Nonnull Long aLong) {
        return storage.containsKey(aLong);
    }

    @Override
    @Nonnull
    public Iterable<User> findAll() {
        return storage.values();
    }

    @Override
    @Nonnull
    public Iterable<User> findAllById(@Nonnull Iterable<Long> longs) {
        List<User> list = new ArrayList<>();
        for (var id : longs) {
            Optional<User> element = this.findById(id);
            element.ifPresent(list::add);
        }
        return list;
    }

    @Override
    public long count() {
        return storage.size();
    }

    @Override
    public void deleteById(@Nonnull Long aLong) {
        storage.remove(aLong);
    }

    @Override
    public void delete(@Nonnull User entity) {
        storage.values().remove(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {
        for (var id : longs) {
            this.deleteById(id);
        }
    }

    @Override
    public void deleteAll(Iterable<? extends User> entities) {
        for (var entity : entities) {
            this.delete(entity);
        }
    }

    @Override
    public void deleteAll() {
        storage.clear();
    }

    public User findByUserLogin(String login) {
        var wrapper = new Object() {
            User entity = null;
        };
        storage.values().forEach((user) -> {
            if (user.getUserLogin().equals(login)) {
                wrapper.entity = user;
            }
        });

        return wrapper.entity;
    }

    public User findByUserEmail(String email) {
        var wrapper = new Object() {
            User entity = null;
        };
        storage.values().forEach((user) -> {
            if (user.getUserEmail().equals(email)) {
                wrapper.entity = user;
            }
        });

        return wrapper.entity;
    }
}
