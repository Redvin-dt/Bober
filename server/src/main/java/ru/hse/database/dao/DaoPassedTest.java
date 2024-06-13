package ru.hse.database.dao;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hse.database.entities.PassedTest;
import ru.hse.database.entities.Question;
import ru.hse.database.entities.Test;
import ru.hse.database.utils.HibernateUtil;

import java.util.List;

public class DaoPassedTest {
    static Logger logger = LoggerFactory.getLogger(DaoPassedTest.class);

    static public PassedTest getPassedTestById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(PassedTest.class, id);
        }
    }
    static public void createOrUpdatePassedTest(PassedTest test) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(test);
            session.getTransaction().commit();
        } catch (IllegalStateException e) {
            logger.error("Error while merging user with id " + test.getTestId(), e);
        }
    }

    static public void deleteTest(PassedTest test) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.remove(test);
            session.getTransaction().commit();
        } catch (IllegalStateException e) {
            logger.error("Error while erasing test {} from db ", test.getTestId(), e);
        }
    }
}
