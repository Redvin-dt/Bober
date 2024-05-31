package ru.hse.server.repository;

import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.hse.database.dao.DaoChapter;
import ru.hse.database.entities.Chapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ChapterRepository implements CrudRepository<Chapter, Long> {

    public <S extends Chapter> S update(@Nonnull S chapter) {
        DaoChapter.createOrUpdateChapter(chapter);
        return chapter;
    }

    @Override
    @Nonnull
    public <S extends Chapter> S save(@Nonnull S chapter) throws EntityNotFoundException {
        if (DaoChapter.getChapterById(chapter.getChapterId()) == null) {
            throw new EntityNotFoundException("chapter not found while try to update");
        }
        DaoChapter.createOrUpdateChapter(chapter);
        return chapter;
    }

    @Override
    @Nonnull
    public <S extends Chapter> Iterable<S> saveAll(Iterable<S> chapters) {
        List<S> list = new ArrayList<>();
        for (var chapter : chapters) {
            list.add(save(chapter));
        }
        return list;
    }

    @Override
    @Nonnull
    public Optional<Chapter> findById(@Nonnull Long id) {
        var chapter = DaoChapter.getChapterById(id);
        if (chapter == null) {
            return Optional.empty();
        } else {
            return Optional.of(chapter);
        }
    }

    @Override
    public boolean existsById(@Nonnull Long id) {
        var chapter = DaoChapter.getChapterById(id);
        return chapter != null;
    }

    @Override
    @Nonnull
    public Iterable<Chapter> findAll() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    @Nonnull
    public Iterable<Chapter> findAllById(@Nonnull Iterable<Long> ids) {
        List<Chapter> chapters = new ArrayList<>();
        for (Long id : ids) {
            var chapter = findById(id);
            chapter.ifPresent(chapters::add);
        }
        return chapters;
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public void deleteById(@Nonnull Long id) {
        var chapter = findById(id);
        chapter.ifPresent(DaoChapter::deleteChapter);
    }

    @Override
    public void delete(@Nonnull Chapter chapter) {
        DaoChapter.deleteChapter(chapter);
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        for (Long id : ids) {
            deleteById(id);
        }
    }

    @Override
    public void deleteAll(Iterable<? extends Chapter> chapters) {
        for (Chapter chapter : chapters) {
            delete(chapter);
        }
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
