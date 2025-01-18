package org.as1iva.repository;

import org.as1iva.entity.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class SessionRepository extends BaseRepository<String, Session> {

    public SessionRepository(SessionFactory sessionFactory) {
        super(Session.class, sessionFactory);
    }
}
