package dev.haedhutner.core.db.cache;

import dev.haedhutner.core.db.Identifiable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

public interface Cache<T extends Identifiable<ID>, ID extends Serializable> {

    void set(ID id, T cacheable);

    void add(T cacheable);

    void addAll(Collection<T> cacheables);

    void remove(T cacheable);

    void removeById(ID id);

    void removeAll(Collection<T> cacheables);

    void removeIf(Predicate<T> condition);

    Optional<T> getById(ID id);

    Collection<T> findAll(Predicate<T> query);

    Optional<T> findOne(Predicate<T> query);

    Collection<T> getAll();
}
