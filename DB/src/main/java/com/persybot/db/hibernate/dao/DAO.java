package com.persybot.db.hibernate.dao;

import com.persybot.db.entity.DBEntity;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Objects;

public abstract class DAO<T extends DBEntity> {
    protected final SessionFactory sessionFactory;
    protected final Class<T> aClass;

    protected DAO(SessionFactory sessionFactory, Class<T> aClass) {
        this.sessionFactory = sessionFactory;
        this.aClass = aClass;
    }

    public T create(final DBEntity entity) {
        Objects.requireNonNull(entity);

        Session session = getCurrentSession();
        try {
            session.beginTransaction();
            session.persist(entity);
            return aClass.cast(entity);
        } finally {
            session.getTransaction().commit();
            session.close();
        }
    }

    public T read(long id) {
        Session session = getCurrentSession();
        try {
            session.beginTransaction();
            return session.get(aClass, id);
        } finally {
            session.getTransaction().commit();
            session.close();
        }
    }

    public T update(final DBEntity entity) {
        Objects.requireNonNull(entity);

        Session session = getCurrentSession();
        try {
            session.beginTransaction();
            session.merge(entity);
            return aClass.cast(entity);

        } finally {
            session.getTransaction().commit();
            session.close();
        }
    }

    public void delete(final DBEntity entity) {
        Objects.requireNonNull(entity);

        Session session = getCurrentSession();
        try {
            session.beginTransaction();
            session.remove(entity);
        } finally {
            session.getTransaction().commit();
            session.close();
        }
    }

    public List<T> readAll() {
        Session session = getCurrentSession();
        try {
            session.beginTransaction();
            CriteriaQuery<T> criteriaQuery = session.getCriteriaBuilder().createQuery(aClass);
            Root<T> entityRoot = criteriaQuery.from(aClass);
            criteriaQuery.select(entityRoot);
            return session.createQuery(criteriaQuery).getResultList();
        } finally {
            session.getTransaction().commit();
            session.close();
        }
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

}
