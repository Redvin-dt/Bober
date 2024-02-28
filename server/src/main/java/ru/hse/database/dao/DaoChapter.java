package ru.hse.database.dao;

import org.hibernate.Session;
import ru.hse.database.entities.Chapter;
import ru.hse.database.entities.Group;
import ru.hse.database.utils.HibernateUtil;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaoChapter {

    static Logger logger = LoggerFactory.getLogger(DaoChapter.class);

    static public Chapter getChapterById(long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        Chapter chapter = session.get(Chapter.class, id);

        session.close();
        return chapter;
    }

    static public Group getGroupByChapter(Chapter chapter) {
        Group group;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            chapter = session.get(Chapter.class, chapter.getChapterId());

            session.beginTransaction();
            group = chapter.getGroupHost();
            session.getTransaction().commit();
        }

        return group;
    }


    static public void createOrUpdateChapter(Chapter chapter) {
        try {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                session.beginTransaction();
                session.merge(chapter);
                session.getTransaction().commit();
            }
        } catch (Exception e) {
            logger.error("Error while merging chapter with id " + chapter.getChapterId(), e);
        }
    }

    static public void deleteChapter(Chapter chapter) {
        Long id = chapter.getChapterId();
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            session.beginTransaction();
            session.remove(chapter);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error while erasing chapter from db " + id, e);
        }
    }
}
