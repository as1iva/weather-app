package org.as1iva.repository;

import org.as1iva.entity.Location;
import org.as1iva.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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

    public Optional<Location> findByCoordinates(User userId, BigDecimal lat, BigDecimal lon) {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery("FROM Location l WHERE l.userId = :userId AND l.latitude = :lat AND l.longitude = :lon", Location.class)
                .setParameter("userId", userId)
                .setParameter("lat", lat)
                .setParameter("lon", lon)
                .uniqueResultOptional();
    }

    public void deleteByCoordinates(User userId, BigDecimal lat, BigDecimal lon) {
        Session session = sessionFactory.getCurrentSession();

        session.createMutationQuery("DELETE FROM Location l WHERE l.userId = :userId AND l.latitude = :lat AND l.longitude = :lon")
                .setParameter("userId", userId)
                .setParameter("lat", lat)
                .setParameter("lon", lon)
                .executeUpdate();
    }
}
