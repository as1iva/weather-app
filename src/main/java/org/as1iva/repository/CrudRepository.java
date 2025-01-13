package org.as1iva.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface CrudRepository<K extends Serializable, E> {

    E save(E entity);

    Optional<E> findById(K id);

    List<E> findAll();

    void update(E entity);

    void delete(K id);
}
