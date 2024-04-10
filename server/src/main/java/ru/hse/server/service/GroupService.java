package ru.hse.server.service;

import com.google.protobuf.InvalidProtocolBufferException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.hse.database.entities.Group;
import ru.hse.server.proto.EntitiesProto.GroupModel;
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

        var user = userRepository.findById(userModel.getId());
        if (user.isEmpty()) {
            throw new EntityNotFoundException("user with that model not found");
        }

        Group group = new Group(groupModel.getName(), groupModel.getPasswordHash(), user.get());

        var result = groupRepository.save(group);
        logger.info("group={} was saved", group);
        return ProtoSerializer.getGroupInfo(result);
    }

    public GroupModel findGroupById(Long id) throws EntityNotFoundException {
        var group = groupRepository.findById(id);
        if (group.isPresent()) {
            logger.debug("find group with id={}", id);
            return ProtoSerializer.getGroupInfo(group.get());
        } else {
            logger.error("group with id={} not found", id);
            throw new EntityNotFoundException("group with id=" + id + " does not exist");
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
}
