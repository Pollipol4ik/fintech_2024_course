package edu.kudago.storage;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
@Component
public class InMemoryStorage<T, ID> {
    private final Map<ID, T> storage = new ConcurrentHashMap<>();

    public Optional<T> findById(ID id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Iterable<T> findAll() {
        return storage.values();
    }

    public T save(ID id, T entity) {
        storage.put(id, entity);
        return entity;
    }

    public void deleteById(ID id) {
        storage.remove(id);
    }

    public boolean existsById(ID id) {
        return storage.containsKey(id);
    }
}
