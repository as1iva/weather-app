package org.as1iva.repository;

import org.as1iva.entity.Location;
import org.as1iva.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LocationRepository extends BaseRepository<Long, Location> {

    @Autowired
    public LocationRepository(SessionFactory sessionFactory) {
        super(Location.class, sessionFactory);
    }

    public List<Location> findAllByUserId(User userId) {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery("FROM Location l WHERE l.userId = :userId", Location.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
