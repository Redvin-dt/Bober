package ru.hse.server.utils;

import ru.hse.database.dao.DaoChapter;
import ru.hse.database.dao.DaoGroup;
import ru.hse.database.entities.*;
import ru.hse.database.dao.DaoUser;
import ru.hse.server.proto.EntitiesProto;
import ru.hse.server.proto.EntitiesProto.UserModel;
import ru.hse.server.proto.EntitiesProto.GroupModel;
import ru.hse.server.proto.EntitiesProto.ChapterModel;
import ru.hse.server.proto.EntitiesProto.QuestionModel;
import ru.hse.server.proto.EntitiesProto.QuestionList;
import ru.hse.server.proto.EntitiesProto.TestModel;
import ru.hse.server.proto.EntitiesProto.TestList;
import ru.hse.server.proto.EntitiesProto.GroupList;
import ru.hse.server.proto.EntitiesProto.UserList;
import ru.hse.server.proto.EntitiesProto.ChapterList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ProtoSerializer {
    static public GroupModel getGroupInfo(Group group) {
        return GroupModel.newBuilder()
                .setId(group.getGroupId())
                .setName(group.getGroupName())
                .setMetaInfo(group.getMetaInfo())
                .setAdmin(getUserInfo(group.getAdmin()))
                .build();
    }

    static public UserModel getUserInfo(User user) {
        return UserModel.newBuilder()
                .setId(user.getUserId())
                .setLogin(user.getUserLogin())
                .setEmail(user.getUserEmail())
                .setMetaInfo(user.getMetaInfo())
                .build();
    }

    static public ChapterModel getChapterInfo(Chapter chapter) {
        return ChapterModel.newBuilder()
                .setId(chapter.getChapterId())
                .setName(chapter.getChapterName())
                .setMetaInfo(chapter.getMetaInfo())
                .build();
    }

    static public QuestionModel getProtoFromQuestion(Question question) {
        return QuestionModel.newBuilder()
                .setId(question.getQuestionId())
                .setQuestion(question.getQuestion())
                .addAllAnswers(question.getAnswers())
                .addAllRightAnswers(question.getRightAnswers())
                .build();
    }

    static public TestModel getProtoFromTest(Test test) {
        return TestModel.newBuilder()
                .setId(test.getTestId())
                .setName(test.getTestName())
                .setQuestions(convertQuestionsToProto(test.getQuestions()))
                .setPosition(test.getPosition())
                .build();
    }

    static public QuestionList convertQuestionsToProto(List<Question> questions) {
        List<QuestionModel> questionModels = new ArrayList<>();
        for (Question question : questions) {
            questionModels.add(getProtoFromQuestion(question));
        }

        return QuestionList.newBuilder()
                .addAllQuestions(questionModels)
                .build();
    }

    static public TestList convertTestsToProto(List<Test> tests) {
        List<TestModel> testModels = new ArrayList<>();
        tests = tests.stream().sorted(Comparator.comparing(Test::getPosition)).collect(Collectors.toList());
        for (Test test : tests) {
            testModels.add(getProtoFromTest(test));
        }

        return TestList.newBuilder()
                .addAllTests(testModels)
                .build();
    }

    static public GroupList convertGroupsToProto(List<Group> groups) {
        List<GroupModel> groupsInfo = new ArrayList<>();
        for (Group group : groups) {
            groupsInfo.add(getGroupInfo(group));
        }
        return GroupList.newBuilder()
                .addAllGroups(groupsInfo)
                .build();
    }

    static public UserList convertUsersToProto(List<User> users) {
        List<UserModel> usersInfo = new ArrayList<>();
        for (User user : users) {
            usersInfo.add(getUserInfo(user));
        }
        return UserList.newBuilder()
                .addAllUsers(usersInfo)
                .build();
    }

    static public ChapterList convertChaptersToProto(List<Chapter> chapters) {
        List<ChapterModel> chaptersInfo = new ArrayList<>();
        for (Chapter chapter : chapters) {
            chaptersInfo.add(getChapterInfo(chapter));
        }
        return ChapterList.newBuilder()
                .addAllChapters(chaptersInfo)
                .build();
    }

    static public UserModel getProtoFromUser(User user) {
        return UserModel.newBuilder()
                .setId(user.getUserId())
                .setLogin(user.getUserLogin())
                .setEmail(user.getUserEmail())
                .setPasswordHash(user.getPasswordHash())
                .setMetaInfo(user.getMetaInfo())
                .setAdminOfGroups(convertGroupsToProto(DaoUser.getGroupsByAdmin(user)))
                .setUserOfGroups(convertGroupsToProto(new ArrayList<>(DaoUser.getGroupsOfUser(user))))
                .build();
    }

    static public GroupModel getProtoFromGroup(Group group) {
        return GroupModel.newBuilder()
                .setId(group.getGroupId())
                .setName(group.getGroupName())
                .setPasswordHash(group.getPasswordHash())
                .setMetaInfo(group.getMetaInfo())
                .setChapters(convertChaptersToProto(DaoGroup.getChaptersByGroup(group)))
                .setAdmin(getUserInfo(DaoGroup.getGroupAdmin(group)))
                .setUsers(convertUsersToProto(new ArrayList<>(DaoGroup.getUsersOfGroup(group))))
                .build();
    }
    static public ChapterModel getProtoFromChapter(Chapter chapter) {
        return ChapterModel.newBuilder()
                .setId(chapter.getChapterId())
                .setName(chapter.getChapterName())
                .setTextFile(chapter.getTextFile())
                .setMetaInfo(chapter.getMetaInfo())
                .setGroup(getGroupInfo(DaoChapter.getGroupByChapter(chapter)))
                .setTests(convertTestsToProto(chapter.getTests()))
                .build();
    }
}
