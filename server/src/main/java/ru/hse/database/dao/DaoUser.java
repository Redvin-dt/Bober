package ru.hse.database.dao;

import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.hse.database.entities.Group;
import ru.hse.database.entities.User;
import ru.hse.database.utils.HibernateUtil;

import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaoUser {
    static Logger logger = LoggerFactory.getLogger(DaoUser.class);

    public static class NotUniqueUserLoginException extends Exception {
        public NotUniqueUserLoginException(String errorMessage, String login) {
            super(errorMessage + login);
        }
    }

    static public User getUserById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, id);
        }
    }

    static public User getUserByLogin(String login) throws NotUniqueUserLoginException {
        Query<User> query;
        List<User> users;
        Session session = HibernateUtil.getSessionFactory().openSession();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<User> criteria = criteriaBuilder.createQuery(User.class);

        Root<User> root = criteria.from(User.class);

        criteria.select(root).where(criteriaBuilder.equal(root.get("userLogin"), login));

        query = session.createQuery(criteria);
        users = query.getResultList();


        if (users.isEmpty()) {
            return null;
        }

        if (users.size() > 1) {
            logger.error("More than 1 users in table with user login {}", login);
            throw new NotUniqueUserLoginException("Found more than 1 user, with login ", login);
        }

        //return users.getFirst();
        return users.stream().findFirst().orElse(null);
    }

    static public User getUserByEmail(String email) throws NotUniqueUserLoginException {
        Query<User> query;
        List<User> users;
        Session session = HibernateUtil.getSessionFactory().openSession();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<User> criteria = criteriaBuilder.createQuery(User.class);

        Root<User> root = criteria.from(User.class);

        criteria.select(root).where(criteriaBuilder.equal(root.get("userEmail"), email));

        query = session.createQuery(criteria);
        users = query.getResultList();

        if (users.isEmpty()) {
            return null;
        }

        if (users.size() > 1) {
            logger.error("More than 1 users in table with user email {}", email);
            throw new NotUniqueUserLoginException("Found more than 1 user, with email ", email);
        }

        //return users.getFirst();
        return users.stream().findFirst().orElse(null);
    }

    static public Set<Group> getGroupsOfUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            user = session.get(User.class, user.getUserId());
            session.beginTransaction();
            Set<Group> groups = user.getGroupsUserSet();
            session.getTransaction().commit();
            return groups;
        }
    }

    static public List<Group> getGroupsByAdmin(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            user = session.get(User.class, user.getUserId());
            session.beginTransaction();
            List<Group> groups = user.getGroupsAdmin();
            session.getTransaction().commit();

            return groups;
        }
    }

    static public void createOrUpdateUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(user);
            session.getTransaction().commit();
        } catch (IllegalStateException e) {
            logger.error("Error while merging user with id " + user.getUserId(), e);
        }
    }

    static public void addUserToGroup(User user, Group group) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            Set<User> users = DaoGroup.getUsersOfGroup(group);

            users.add(user);

            group.setUsersSet(users);

            session.merge(user);
            session.merge(group);

            session.getTransaction().commit();
        } catch (IllegalStateException e) {
            logger.error("Error while erasing user {} from group {}", user.getUserId(), +group.getGroupId(), e);
        }
    }

    static public void deleteUserFromGroup(User user, Group group) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            Set<User> users = DaoGroup.getUsersOfGroup(group);
            Set<Group> groups = DaoUser.getGroupsOfUser(user);

            users.removeIf(user1 -> (user1.hashCode() == user.hashCode()));
            groups.removeIf(group1 -> (group1.hashCode() == group.hashCode()));

            user.setGroupsUserSet(groups);
            group.setUsersSet(users);


            session.merge(user);
            session.merge(group);

            session.getTransaction().commit();
        } catch (IllegalStateException e) {
            logger.error("Error while erasing user {} from group {}", user.getUserId(), +group.getGroupId(), e);
        }
    }

    static public void deleteUser(User user) {
        long id = user.getUserId();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.remove(user);
            session.getTransaction().commit();
        } catch (IllegalStateException e) {
            logger.error("Error while erasing user {} from db ", id, e);
        }
    }
}
