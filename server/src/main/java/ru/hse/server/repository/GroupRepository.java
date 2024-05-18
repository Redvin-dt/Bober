package ru.hse.server.repository;

import jakarta.annotation.Nonnull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.hse.database.dao.DaoGroup;
import ru.hse.database.entities.Group;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class GroupRepository implements CrudRepository<Group, Long> {
    @Override
    @Nonnull
    public <S extends Group> S save(@Nonnull S group) {
        DaoGroup.createOrUpdateGroup(group);
        return group;
    }

    @Override
    @Nonnull
    public <S extends Group> Iterable<S> saveAll(Iterable<S> groups) {
        List<S> list = new ArrayList<>();
        for (var group : groups) {
            list.add(save(group));
        }
        return list;
    }

    public <S extends Group> S update(@Nonnull S group) {
        if (DaoGroup.getGroupById(group.getGroupId()) == null) {
            return null; // TODO: delete or do smth better
        }

        DaoGroup.createOrUpdateGroup(group);
        return group;
    }

    @Override
    @Nonnull
    public Optional<Group> findById(@Nonnull Long id) {
        var group = DaoGroup.getGroupById(id);
        if (group == null) {
            return Optional.empty();
        } else {
            return Optional.of(group);
        }
    }

    @Nonnull
    public List<Group> findByName(@Nonnull String groupName) {
        return DaoGroup.getGroupsByName(groupName);
    }

    @Nonnull
    public List<Group> findByPrefixName(@Nonnull String groupName) {
        return DaoGroup.getGroupsByNamePrefix(groupName);
    }

    @Override
    public boolean existsById(@Nonnull Long id) {
        var group = DaoGroup.getGroupById(id);
        return group != null;
    }

    @Override
    @Nonnull
    public Iterable<Group> findAll() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    @Nonnull
    public Iterable<Group> findAllById(@Nonnull Iterable<Long> ids) {
        List<Group> list = new ArrayList<>();
        for (var id : ids) {
            var group = findById(id);
            group.ifPresent(list::add);
        }
        return list;
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public void deleteById(@Nonnull Long id) {
        var group = findById(id);
        group.ifPresent(DaoGroup::deleteGroup);
    }

    @Override
    public void delete(@Nonnull Group group) {
        DaoGroup.deleteGroup(group);
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        for (var id : ids) {
            deleteById(id);
        }
    }

    @Override
    public void deleteAll(Iterable<? extends Group> groups) {
        for (var group : groups) {
            delete(group);
        }
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
