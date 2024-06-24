package ru.hse.server.service;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.hse.database.entities.Group;
import ru.hse.database.entities.User;
import ru.hse.server.exception.AccessException;
import ru.hse.server.exception.EntityUpdateException;
import ru.hse.server.repository.GroupRepository;
import ru.hse.server.repository.UserRepository;

@Service
public class InviteService {
    private final Logger logger = LoggerFactory.getLogger(InviteService.class);

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public InviteService(@Qualifier("userDatabaseRepository") UserRepository userRepository, GroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    public void acceptInvite(Long userId, Long groupId) throws EntityUpdateException {
        var user = getUserByID(userId);
        var group = getGroup(groupId);

        var invitations = user.getInvitations();

        if (!invitations.contains(group)) {
            logger.error("user has not invitations to group with id={}", groupId);
            throw new EntityNotFoundException("user has not invitations to group with id=" + groupId);
        }

        invitations.remove(group);
        user.getGroupsUserSet().add(group);
        updateEntities(user);
    }

    public void declineInvite(Long userId, Long groupId) throws EntityUpdateException {
        var user = getUserByID(userId);
        var group = getGroup(groupId);

        var invitations = user.getInvitations();
        if (!invitations.contains(group)) {
            logger.error("user has not invitations to group with id={}", groupId);
            throw new EntityNotFoundException("user has not invitations to group with id=" + groupId);
        }

        invitations.remove(group);

        updateEntities(user);
    }

    public void createInvite(String userLogin, Long groupId) throws AccessException, EntityUpdateException {
        var user = getUserByLogin(userLogin);
        var group = getGroup(groupId);

        user.addInvitation(group);
        updateEntities(user);
    }

    private User getUserByLogin(String userLogin) {
        var user = userRepository.findByUserLogin(userLogin);
        if (user == null) {
            logger.error("user not found, login={}", userLogin);
            throw new EntityNotFoundException("user not found, login=" + userLogin);
        }

        return user;
    }


    private User getUserByID(Long userId) {
        var user = userRepository.findById(userId);
        if (user.isEmpty()) {
            logger.error("user not found, id={}", userId);
            throw new EntityNotFoundException("group not found, id=" + userId);
        }

        return user.get();
    }

    private Group getGroup(Long groupId) {
        var group = groupRepository.findById(groupId);
        if (group.isEmpty()) {
            logger.error("group not found, id={}", groupId);
            throw new EntityNotFoundException("group not found, id=" + groupId);
        }

        return group.get();
    }

    private void updateEntities(User user) throws EntityUpdateException {
        if (userRepository.update(user) == null) {
            throw new EntityUpdateException("failed while update user with id=" + user.getUserId());
        }
    }
}
