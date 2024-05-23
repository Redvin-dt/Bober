package ru.hse.server.repository;

import jakarta.annotation.Nonnull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.hse.database.dao.DaoTest;
import ru.hse.database.entities.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TestRepository implements CrudRepository<Test, Long>  {
    @Nonnull
    @Override
    public <S extends Test> S save(@Nonnull S test) {
        DaoTest.createOrUpdateTest(test, test.getQuestions());
        return test;
    }

    @Nonnull
    @Override
    public <S extends Test> Iterable<S> saveAll(Iterable<S> tests) {
        for (var test : tests) {
            save(test);
        }
        return tests;
    }

    @Nonnull
    @Override
    public Optional<Test> findById(@Nonnull Long testId) {
        var test = DaoTest.getTestById(testId);
        if (test == null) {
            return Optional.empty();
        } else {
            return Optional.of(test);
        }
    }

    @Override
    public boolean existsById(@Nonnull Long testId) {
        var test = DaoTest.getTestById(testId);
        return test != null;
    }

    @Nonnull
    @Override
    public Iterable<Test> findAll() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Nonnull
    @Override
    public Iterable<Test> findAllById(Iterable<Long> testIds) {
        List<Test> tests = new ArrayList<>();
        for (var testId : testIds) {
            var test = DaoTest.getTestById(testId);
            if (test != null) {
                tests.add(test);
            }
        }
        return tests;
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public void deleteById(@Nonnull Long testId) {
        var test = DaoTest.getTestById(testId);
        if (test != null) {
            DaoTest.deleteTest(test);
        }
    }

    @Override
    public void delete(@Nonnull Test test) {
        DaoTest.deleteTest(test);
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> testIds) {
        for (var testId : testIds) {
            var test = DaoTest.getTestById(testId);
            if (test != null) {
                DaoTest.deleteTest(test);
            }
        }
    }

    @Override
    public void deleteAll(Iterable<? extends Test> tests) {
        for (var test : tests) {
            DaoTest.deleteTest(test);
        }
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
