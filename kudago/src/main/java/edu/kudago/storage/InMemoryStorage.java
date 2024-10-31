package edu.kudago.storage;


import edu.kudago.observer.Observable;
import edu.kudago.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStorage<T, ID> implements Observable<T> {
    private final Map<ID, T> storage = new ConcurrentHashMap<>();
    private final List<Observer<T>> observers = new ArrayList<>();

    public Optional<T> findById(ID id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Iterable<T> findAll() {
        return storage.values();
    }

    public T save(ID id, T entity) {
        storage.put(id, entity);
        notifyObservers(List.of(entity));
        return entity;
    }

    public void deleteById(ID id) {
        T entity = storage.remove(id);
        if (entity != null) {
            notifyObservers(List.of(entity));
        }
    }

    public boolean existsById(ID id) {
        return storage.containsKey(id);
    }

    @Override
    public void addObserver(Observer<T> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<T> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(List<T> data) {
        observers.forEach(observer -> observer.update(data));
    }
}
