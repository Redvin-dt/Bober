package ru.hse.database.dao;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hse.database.entities.Question;
import ru.hse.database.entities.Test;
import ru.hse.database.entities.User;
import ru.hse.database.utils.HibernateUtil;

import javax.management.Query;
import java.util.List;

public class DaoTest {
    static Logger logger = LoggerFactory.getLogger(DaoTest.class);

    static public Test getTestById(long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        return session.get(Test.class, id);
    }
    static public void createOrUpdateTest(Test test, List<Question> questions) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            test.setQuestions(questions);
            session.merge(test);
            session.getTransaction().commit();
        } catch (IllegalStateException e) {
            logger.error("Error while merging user with id " + test.getTestId(), e);
        }
    }

    static public void deleteTest(Test test) {
        long id = test.getTestId();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.remove(test);
            session.getTransaction().commit();
        } catch (IllegalStateException e) {
            logger.error("Error while erasing test {} from db ", id, e);
        }
    }
}
