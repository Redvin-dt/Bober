package ru.hse.server.service;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hse.database.entities.Group;
import ru.hse.server.repository.GroupRepository;

@Service
public class GroupService {

    static private final Logger logger = LoggerFactory.getLogger(GroupService.class);
    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public void createGroup(Group group) {
        groupRepository.save(group);
        logger.info("group={} was saved", group);
    }

    public Group findGroupById(Long id) throws EntityNotFoundException {
        var group = groupRepository.findById(id);
        if (group.isPresent()) {
            logger.debug("find group with id={}", id);
            return group.get();
        } else {
            logger.error("group with id={} not found", id);
            throw new EntityNotFoundException("group with id=" + id + " does not exist");
        }
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
