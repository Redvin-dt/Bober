package ru.hse.server.service;

import ru.hse.database.entities.Question;
import ru.hse.database.entities.Test;
import ru.hse.server.proto.EntitiesProto;
import ru.hse.server.proto.EntitiesProto.ChapterModel;
import ru.hse.server.proto.EntitiesProto.GroupModel;
import ru.hse.database.entities.Chapter;
import ru.hse.server.repository.ChapterRepository;
import ru.hse.server.repository.GroupRepository;
import ru.hse.server.repository.TestRepository;
import ru.hse.server.utils.ProtoSerializer;

import com.google.protobuf.InvalidProtocolBufferException;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChapterService {
    static private final Logger logger = LoggerFactory.getLogger(ChapterService.class);
    private final ChapterRepository chapterRepository;
    private final GroupRepository groupRepository;
    private final TestRepository testRepository;
    public ChapterService(ChapterRepository chapterRepository, @Qualifier("groupRepository") GroupRepository groupRepository, @Qualifier("testRepository") TestRepository testRepository) {
        this.chapterRepository = chapterRepository;
        this.groupRepository = groupRepository;
        this.testRepository = testRepository;
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

        if (chapterModel.hasMetaInfo()) {
            chapter.setMetaInfo(chapter.getMetaInfo());
        }

        if (chapterModel.hasDeadlineTs()) {
            chapter.setDeadlineTs(chapterModel.getDeadlineTs());
        }
        chapterRepository.save(chapter);

        if (chapterModel.hasTests()) {
            for (var test : chapterModel.getTests().getTestsList()) {
                Test chapterTest = getTest(test, chapter);
                testRepository.save(chapterTest);
            }
        }

        logger.info("chapter={} was saved", chapter);

        return ProtoSerializer.getChapterInfo(chapter);
    }

    public ChapterModel addTest(EntitiesProto.TestList tests, Long chapterId) throws EntityExistsException,
            InvalidProtocolBufferException {
        Optional<Chapter> chapter = chapterRepository.findById(chapterId);
        if (chapter.isEmpty()) {
            logger.error("chapter with id {} is empty", chapterId);
            return null;
        }

        for (var test : tests.getTestsList()) {
            Test chapterTest = getTest(test, chapter.get());
            testRepository.save(chapterTest);
        }

        chapter = chapterRepository.findById(chapterId);
        if (chapter.isEmpty()) {
            logger.error("error while saving chapter with id {}", chapterId);
            return null;
        }

        return ProtoSerializer.getChapterInfo(chapter.get());
    }

    public ChapterModel changeDeadline(Long deadlineTs, Long chapterId) throws EntityExistsException {
        Optional<Chapter> chapter = chapterRepository.findById(chapterId);
        if (chapter.isEmpty()) {
            logger.error("chapter with id {} is empty", chapterId);
            return null;
        }

        chapter.get().setDeadlineTs(deadlineTs);
        chapterRepository.save(chapter.get());

        return ProtoSerializer.getChapterInfo(chapter.get());
    }

    private static Test getTest(EntitiesProto.TestModel test, Chapter chapter) throws InvalidProtocolBufferException {
        if (!test.hasName() || !test.hasPosition() || !test.hasQuestions()) {
            throw new InvalidProtocolBufferException("invalid protobuf test in chapter creation");
        }
        List<Question> questionList = new ArrayList<>();
        Test chapterTest = new Test(test.getName(), test.getPosition(), chapter);
        for (var questionModel : test.getQuestions().getQuestionsList()) {
            if (!questionModel.hasQuestion()
                    || questionModel.getRightAnswersCount() == 0 || questionModel.getAnswersCount() == 0) {
                throw new InvalidProtocolBufferException("invalid protobuf question for test in chapter creation");
            }
            Question question = new Question(questionModel.getQuestion(),
                    questionModel.getRightAnswersList(),questionModel.getAnswersList(), chapterTest);
            questionList.add(question);
        }
        chapterTest.setQuestions(questionList);
        chapterTest.setSecondsForTest(test.getSecondsForTest());
        return chapterTest;
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
