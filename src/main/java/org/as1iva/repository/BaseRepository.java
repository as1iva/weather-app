package org.as1iva.repository;

import jakarta.persistence.criteria.CriteriaQuery;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class BaseRepository<K extends Serializable, E> implements CrudRepository<K, E> {

    private final Class<E> entityClass;

    protected final SessionFactory sessionFactory;

    @Override
    public E save(E entity) {
        Session session = sessionFactory.getCurrentSession();

        session.persist(entity);

        return entity;
    }

    @Override
    public Optional<E> findById(K id) {
        Session session = sessionFactory.getCurrentSession();

        return Optional.ofNullable(session.find(entityClass, id));
    }

    @Override
    public List<E> findAll() {
        Session session = sessionFactory.getCurrentSession();

        CriteriaQuery<E> criteria = session.getCriteriaBuilder().createQuery(entityClass);
        criteria.from(entityClass);

        return session.createQuery(criteria).getResultList();
    }

    @Override
    public void update(E entity) {
        Session session = sessionFactory.getCurrentSession();

        session.merge(entity);
    }

    @Override
    public void delete(K id) {
        Session session = sessionFactory.getCurrentSession();
        E entity = findById(id).orElseThrow(()-> new RuntimeException());

        session.remove(entity);
    }
}
