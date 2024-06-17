package ru.hse.database.dao;

import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.query.SelectionQuery;
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
    private static final int MAX_RESULTS = 20;
    static Logger logger = LoggerFactory.getLogger(DaoGroup.class);

    static public Group getGroupById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Group.class, id);
        }
    }

    static public List<Group> getGroupsByName(String groupName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Group> criteria = criteriaBuilder.createQuery(Group.class);

            Root<Group> root = criteria.from(Group.class);

            criteria.select(root).where(criteriaBuilder.equal(root.get("groupName"), groupName));

            Query<Group> query = session.createQuery(criteria);

            return query.getResultList();
        }
    }

    static public List<Group> getGroupsByNamePrefix(String groupPrefix) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Group> criteria = criteriaBuilder.createQuery(Group.class);

            Root<Group> root = criteria.from(Group.class);

            criteria.select(root).where(criteriaBuilder.like(root.get("groupName"), groupPrefix + "%"));
            Query<Group> query = session.createQuery(criteria);
            query.setMaxResults(MAX_RESULTS);
            return query.getResultList();
        }
    }

    static public User getGroupAdmin(Group group) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            group = session.get(Group.class, group.getGroupId());

            session.beginTransaction();
            User user = group.getAdmin();
            session.getTransaction().commit();

            return user;
        }
    }

    static public Set<User> getUsersOfGroup(Group group) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            group = session.get(Group.class, group.getGroupId());

            session.beginTransaction();
            Set<User> users = group.getUsersSet();
            session.getTransaction().commit();

            return users;
        }
    }

    static public List<Chapter> getChaptersByGroup(Group group) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            group = session.get(Group.class, group.getGroupId());

            session.beginTransaction();
            List<Chapter> chapters = group.getChapters();
            session.getTransaction().commit();

            return chapters;
        }
    }

    static public void createOrUpdateGroup(Group group) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(group);
            session.getTransaction().commit();
        } catch (IllegalStateException e) {
            logger.error("Error while merging group with id {}", group.getGroupId(), e);
        }
    }

    static public void deleteGroup(Group group) {
        long id = group.getGroupId();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.remove(group);
            session.getTransaction().commit();
        } catch (IllegalStateException e) {
            logger.error("Error while erasing group with id {} from db ", id, e);
        }
    }
}
