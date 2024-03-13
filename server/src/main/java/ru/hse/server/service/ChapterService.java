package ru.hse.server.service;

import ru.hse.server.proto.EntitiesProto;
import ru.hse.server.proto.EntitiesProto.ChapterModel;
import ru.hse.server.proto.EntitiesProto.GroupModel;
import ru.hse.database.entities.Chapter;
import ru.hse.server.repository.ChapterRepository;
import ru.hse.server.repository.GroupRepository;
import ru.hse.server.utils.ProtoSerializer;

import com.google.protobuf.InvalidProtocolBufferException;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChapterService {
    static private final Logger logger = LoggerFactory.getLogger(ChapterService.class);
    private final ChapterRepository chapterRepository;
    private final GroupRepository groupRepository;
    public ChapterService(ChapterRepository chapterRepository, @Qualifier("groupRepository") GroupRepository groupRepository) {
        this.chapterRepository = chapterRepository;
        this.groupRepository = groupRepository;
    }

    public ChapterModel createChapter(ChapterModel chapterModel) throws EntityExistsException,
            InvalidProtocolBufferException {

        if (!chapterModel.hasName() || !chapterModel.hasGroup()){
            throw new InvalidProtocolBufferException("invalid protobuf chapter, require group and name");
        }

        GroupModel groupModel = chapterModel.getGroup();

        if (!groupModel.hasId()) {
            throw new InvalidProtocolBufferException("invalid protobuf group for chapter creation, require id");
        }

        var group = groupRepository.findById(groupModel.getId());

        if (group.isEmpty()) {
            throw new EntityNotFoundException("group with that model not found");
        }

        Chapter chapter = new Chapter(chapterModel.getName(), group.get());

        chapterRepository.save(chapter);

        logger.info("chapter={} was saved", chapter);

        return ProtoSerializer.getChapterInfo(chapter);
    }

    public ChapterModel getChapterByID(Long id) throws EntityNotFoundException {
        Optional<Chapter> chapter = chapterRepository.findById(id);
        if (chapter.isEmpty()) {
            throw new EntityNotFoundException("chapter with that id=" + id + " not found");
        }

        return ProtoSerializer.getProtoFromChapter(chapter.get());
    }

    public void deleteChapter(Long id) throws EntityNotFoundException {
        if (chapterRepository.existsById(id)) {
            logger.debug("find chapter with id={}", id);
            chapterRepository.deleteById(id);
        } else {
            logger.error("chapter with id={} not found", id);
            throw new EntityNotFoundException("chapter with id=" + id + " does not exist");
        }
    }
}
