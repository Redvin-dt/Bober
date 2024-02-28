package ru.hse.database.dao;

import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.hse.database.entities.Chapter;
import ru.hse.database.entities.Group;
import ru.hse.database.entities.User;
import ru.hse.database.utils.HibernateUtil;

import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class DaoGroup {

    static Logger logger = LoggerFactory.getLogger(DaoGroup.class);
    static public Group getGroupById(long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        Group group = session.get(Group.class, id);

        session.close();
        return group;
    }
    static public List<Group> getGroupsByName(String goupName) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Group> criteria = criteriaBuilder.createQuery(Group.class);

        Root<Group> root = criteria.from(Group.class);

        criteria.select(root).where(criteriaBuilder.equal(root.get("groupName"), goupName));

        Query<Group> query = session.createQuery(criteria);

        List<Group> groups = query.getResultList();
        session.close();

        return groups;
    }

    static public User getGroupAdmin(Group group) {
        User user;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            group = session.get(Group.class, group.getGroupId());

            session.beginTransaction();
            user = group.getAdmin();
            session.getTransaction().commit();
        }

        return user;
    }

    static public Set<User> getUsersOfGroup(Group group) {
        Set<User> users;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            group = session.get(Group.class, group.getGroupId());
            session.beginTransaction();
            users = group.getUsersSet();
            session.getTransaction().commit();
        }

        return users;
    }

    static public List<Chapter> getChaptersByGroup(Group group) {
        List<Chapter> chapters;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            group = session.get(Group.class, group.getGroupId());

            session.beginTransaction();
            chapters = group.getChapters();
            session.getTransaction().commit();
        }

        return chapters;
    }

    static public void createOrUpdateGroup(Group group) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            session.beginTransaction();
            session.merge(group);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error while merging group with id " + group.getGroupId(), e);
        }
    }

    static public void deleteGroup(Group group) {
        Long id = group.getGroupId();
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            session.beginTransaction();
            session.remove(group);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error while erasing group from db " + id, e);
        }
    }
}
