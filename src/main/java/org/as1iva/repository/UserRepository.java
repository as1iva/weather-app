package org.as1iva.repository;

import org.as1iva.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository extends BaseRepository<Long, User>{

    @Autowired
    public UserRepository(SessionFactory sessionFactory) {
        super(User.class, sessionFactory);
    }

    public Optional<User> findByLogin(String login) {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery("FROM User u WHERE u.login = :login", User.class)
                .setParameter("login", login)
                .uniqueResultOptional();
    }
}