package org.as1iva.repository;

import org.as1iva.entity.Location;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LocationRepository extends BaseRepository<Long, Location> {

    @Autowired
    public LocationRepository(SessionFactory sessionFactory) {
        super(Location.class, sessionFactory);
    }
}
