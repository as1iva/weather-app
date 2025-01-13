package org.as1iva.repository;

import org.as1iva.entity.User;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository extends BaseRepository<Long, User>{

    @Autowired
    public UserRepository(SessionFactory sessionFactory) {
        super(User.class, sessionFactory);
    }
}