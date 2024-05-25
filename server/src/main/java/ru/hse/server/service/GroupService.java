package ru.hse.server.service;

import com.google.protobuf.InvalidProtocolBufferException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.hse.database.entities.Group;
import ru.hse.database.entities.User;
import ru.hse.server.exception.AccessException;
import ru.hse.server.exception.EntityUpdateException;
import ru.hse.server.proto.EntitiesProto;
import ru.hse.server.proto.EntitiesProto.GroupModel;
import ru.hse.server.proto.EntitiesProto.UserModel;
import ru.hse.server.proto.EntitiesProto.GroupList;
import ru.hse.server.repository.GroupRepository;
import ru.hse.server.repository.UserRepository;
import ru.hse.server.utils.ProtoSerializer;

@Service
public class GroupService {

    static private final Logger logger = LoggerFactory.getLogger(GroupService.class);
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public GroupService(GroupRepository groupRepository, @Qualifier("userDatabaseRepository") UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    public GroupModel createGroup(GroupModel groupModel) throws InvalidProtocolBufferException {
        if (!groupModel.hasAdmin() || !groupModel.hasName() || !groupModel.hasPasswordHash()) {
            throw new InvalidProtocolBufferException("invalid protobuf group, require admin, group name and password");
        }

        var userModel = groupModel.getAdmin();

        if (!userModel.hasId()) {
            throw new InvalidProtocolBufferException("invalid protobuf group, require id in admin filed");
        }

        var user = getUserById(userModel.getId());

        Group group = new Group(groupModel.getName(), groupModel.getPasswordHash(), user);

        var result = groupRepository.save(group);
        logger.info("group={} was saved", group);
        return ProtoSerializer.getGroupInfo(result);
    }

    public GroupModel enterGroup(String userLogin, GroupModel groupModel) throws InvalidProtocolBufferException,
            EntityNotFoundException, AccessException, EntityUpdateException {
        if (!groupModel.hasId()) {
            throw new InvalidProtocolBufferException("invalid protocol buffer on entry group, group must have id");
        }

        var user = getUserByLogin(userLogin);
        var group = getGroupById(groupModel.getId());

        if (group.getUsersSet().contains(user)) {
            return ProtoSerializer.getProtoFromGroup(group);
        }

        if (!groupModel.hasPasswordHash()) {
            throw new InvalidProtocolBufferException("invalid protocol buffer on entry group, you are not member of group, add password");
        }

        if (!group.getPasswordHash().equals(groupModel.getPasswordHash())) { // TODO: add hashing
            throw new AccessException("invalid password for group");
        }

        group.addUser(user);
        if (groupRepository.update(group) == null) {
            throw new EntityUpdateException("can not update group");
        }

        return ProtoSerializer.getProtoFromGroup(group);
    }

    public GroupModel findGroupById(Long id) throws EntityNotFoundException {
        var group = getGroupById(id);
        logger.debug("find group with id={}", id);
        return ProtoSerializer.getGroupInfo(group);
    }

    public void addUser(Long userId, Long groupId) throws EntityNotFoundException, EntityUpdateException {
        var user = getUserById(userId);
        var group = getGroupById(groupId);

        var userSet = group.getUsersSet();
        if (userSet.contains(user)) {
            throw new EntityExistsException("user already in group");
        }

        group.addUser(user);
        if (groupRepository.update(group) == null) {
            throw new EntityUpdateException("can not update group");
        }
    }

    public void deleteUser(Long userId, Long groupId) throws EntityNotFoundException, EntityUpdateException {
        var user = getUserById(userId);
        var group = getGroupById(groupId);

        var userSet = group.getUsersSet();
        if (!userSet.contains(user)) {
            throw new EntityNotFoundException("user not in this group");
        }

        group.removeUser(user);
        if (groupRepository.update(group) == null) {
            throw new EntityUpdateException("can not update group");
        }
    }

    public GroupList findGroupsByName(String groupName) throws EntityNotFoundException {
        var groups = groupRepository.findByName(groupName);
        logger.debug("find groups with group name={}", groupName);
        return ProtoSerializer.convertGroupsToProto(groups);
    }

    public GroupList findGroupsByPrefixOfName(String groupName) throws EntityNotFoundException {
        var groups = groupRepository.findByPrefixName(groupName);
        logger.debug("find groups with prefix group name={}", groupName);
        return ProtoSerializer.convertGroupsToProto(groups);
    }

    public void deleteGroup(Long id) throws EntityNotFoundException {
        if (groupRepository.existsById(id)) {
            logger.debug("find group with id={}", id);
            groupRepository.deleteById(id);
        } else {
            logger.error("group with id={} not found", id);
            throw new EntityNotFoundException("group with id=" + id + " does not exist");
        }
    }

    private User getUserById(Long id) throws EntityNotFoundException {
        var user = userRepository.findById(id);
        if (user.isEmpty()) {
            logger.error("user with id={} not found", id);
            throw  new EntityNotFoundException("user with id=" + id + " does not exist");
        }

        return user.get();
    }

    private Group getGroupById(Long id) throws EntityNotFoundException {
        var group = groupRepository.findById(id);
        if (group.isEmpty()) {
            logger.error("group with id={} not found", id);
            throw new EntityNotFoundException("group with id=" + id + " does not exist");
        }

        return group.get();
    }

    private User getUserByLogin(String userLogin) throws EntityNotFoundException {
        var user = userRepository.findByUserLogin(userLogin);
        if (user == null) {
            logger.error("user with login={} not found", userLogin);
            throw new EntityNotFoundException("user with login=" + userLogin + " does not exist");
        }

        return user;
    }
}
