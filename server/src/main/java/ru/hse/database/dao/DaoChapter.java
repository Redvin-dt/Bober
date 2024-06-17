package ru.hse.database.dao;

import org.hibernate.Session;
import ru.hse.database.entities.Chapter;
import ru.hse.database.entities.Group;
import ru.hse.database.utils.HibernateUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaoChapter {

    static Logger logger = LoggerFactory.getLogger(DaoChapter.class);

    static public Chapter getChapterById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Chapter.class, id);
        }
    }

    static public Group getGroupByChapter(Chapter chapter) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            chapter = session.get(Chapter.class, chapter.getChapterId());

            session.beginTransaction();
            Group group = chapter.getGroupHost();
            session.getTransaction().commit();

            return group;
        }
    }


    static public void createOrUpdateChapter(Chapter chapter) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(chapter);
            session.getTransaction().commit();
        }
        catch (IllegalStateException e) {
            logger.error("Error while merging chapter with id {}", chapter.getChapterId(), e);
        }
    }

    static public void deleteChapter(Chapter chapter) {
        long id = chapter.getChapterId();
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            session.beginTransaction();
            session.remove(chapter);
            session.getTransaction().commit();
        } catch (IllegalStateException e) {
            logger.error("Error while erasing chapter with id {} from db ", id, e);
        }
    }
}
