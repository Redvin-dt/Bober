package ru.hse.server.repository;

import jakarta.annotation.Nonnull;
import ru.hse.server.entity.UserEntity;

import java.util.*;

/**
 *  class only for test
 */
public class UserLocalRepository implements UserRepository {

    Map<Long, UserEntity> storage = new TreeMap<>();
    private static long counter = 1;

    @Override
    @Nonnull
    @SuppressWarnings("unchecked")
    public <S extends UserEntity> S save(@Nonnull S entity) {
        return (S) Objects.requireNonNull(storage.put(counter++, entity));
    }

    @Override
    @Nonnull
    public <S extends UserEntity> Iterable<S> saveAll(Iterable<S> entities) {
        List<S> list = new ArrayList<>();
        for (var entity : entities) {
            list.add(save(entity));
        }
        return list;
    }

    @Override
    @Nonnull
    public Optional<UserEntity> findById(@Nonnull Long aLong) {
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
    public Iterable<UserEntity> findAll() {
        return storage.values();
    }

    @Override
    @Nonnull
    public Iterable<UserEntity> findAllById(@Nonnull Iterable<Long> longs) {
        List<UserEntity> list = new ArrayList<>();
        for (var id : longs) {
            Optional<UserEntity> element = this.findById(id);
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
    public void delete(@Nonnull UserEntity entity) {
        storage.values().remove(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {
        for (var id : longs) {
            this.deleteById(id);
        }
    }

    @Override
    public void deleteAll(Iterable<? extends UserEntity> entities) {
        for (var entity : entities) {
            this.delete(entity);
        }
    }

    @Override
    public void deleteAll() {
        storage.clear();
    }

    public UserEntity findByLogin(String login) {
        var wrapper = new Object() {
            UserEntity entity = null;
        };
        storage.values().forEach((user) -> {
            if (user.getLogin().equals(login)) {
                wrapper.entity = user;
            }
        });

        return wrapper.entity;
    }
}
