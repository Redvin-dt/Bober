package ru.hse.server.utils;

import ru.hse.database.dao.DaoChapter;
import ru.hse.database.dao.DaoGroup;
import ru.hse.database.entities.*;
import ru.hse.database.dao.DaoUser;
import ru.hse.server.proto.EntitiesProto.UserProto;
import ru.hse.server.proto.EntitiesProto.GroupProto;
import ru.hse.server.proto.EntitiesProto.ChapterProto;
import ru.hse.server.proto.EntitiesProto.GroupList;
import ru.hse.server.proto.EntitiesProto.UserList;
import ru.hse.server.proto.EntitiesProto.ChapterList;

import java.util.ArrayList;
import java.util.List;

public class ProtoSerializer {
    static public GroupProto getGroupInfo(Group group) {
        return GroupProto.newBuilder()
                .setId(group.getGroupId())
                .setName(group.getGroupName())
                .setPasswordHash(group.getPasswordHash())
                .setMetaInfo(group.getMetaInfo())
                .build();
    }

    static public UserProto getUserInfo(User user) {
        return UserProto.newBuilder()
                .setId(user.getUserId())
                .setLogin(user.getUserLogin())
                .setPasswordHash(user.getPasswordHash())
                .setMetaInfo(user.getMetaInfo())
                .build();
    }

    static public ChapterProto getChapterInfo(Chapter chapter) {
        return ChapterProto.newBuilder()
                .setId(chapter.getChapterId())
                .setName(chapter.getChapterName())
                .setMetaInfo(chapter.getMetaInfo())
                .build();
    }

    static public GroupList convertToGroupList(List<Group> groups) {
        List<GroupProto> groupsInfo = new ArrayList<>();
        for (Group group : groups) {
            groupsInfo.add(getGroupInfo(group));
        }
        return GroupList.newBuilder()
                .addAllGroups(groupsInfo)
                .build();
    }

    static public UserList convertToUserList(List<User> users) {
        List<UserProto> usersInfo = new ArrayList<>();
        for (User user : users) {
            usersInfo.add(getUserInfo(user));
        }
        return UserList.newBuilder()
                .addAllUsers(usersInfo)
                .build();
    }

    static public ChapterList convertToChapterList(List<Chapter> chapters) {
        List<ChapterProto> chaptersInfo = new ArrayList<>();
        for (Chapter chapter : chapters) {
            chaptersInfo.add(getChapterInfo(chapter));
        }
        return ChapterList.newBuilder()
                .addAllChapters(chaptersInfo)
                .build();
    }

    static public UserProto getProtoFromUser(User user) {
        return UserProto.newBuilder()
                .setId(user.getUserId())
                .setLogin(user.getUserLogin())
                .setPasswordHash(user.getPasswordHash())
                .setMetaInfo(user.getMetaInfo())
                .setAdminOfGroups(convertToGroupList(DaoUser.getGroupsByAdmin(user)))
                .setUserOfGroups(convertToGroupList(new ArrayList<>(DaoUser.getGroupsOfUser(user))))
                .build();
    }

    static public GroupProto getProtoFromGroup(Group group) {
        return GroupProto.newBuilder()
                .setId(group.getGroupId())
                .setName(group.getGroupName())
                .setPasswordHash(group.getPasswordHash())
                .setMetaInfo(group.getMetaInfo())
                .setChapters(convertToChapterList(DaoGroup.getChaptersByGroup(group)))
                .setAdmin(getUserInfo(DaoGroup.getGroupAdmin(group)))
                .setUsers(convertToUserList(new ArrayList<>(DaoGroup.getUsersOfGroup(group))))
                .build();
    }
    static public ChapterProto getProtoFromChapter(Chapter chapter) {
        return ChapterProto.newBuilder()
                .setId(chapter.getChapterId())
                .setName(chapter.getChapterName())
                .setText(chapter.getTextOfChapter())
                .setTestData(chapter.getTestData())
                .setMetaInfo(chapter.getMetaInfo())
                .setGroup(getGroupInfo(DaoChapter.getGroupByChapter(chapter)))
                .build();
    }
}
