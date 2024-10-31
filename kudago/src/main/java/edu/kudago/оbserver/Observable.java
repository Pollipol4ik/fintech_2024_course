package edu.kudago.îbserver;

import java.util.List;

public interface Observable<T> {
    void addObserver(Observer<T> observer);
    void removeObserver(Observer<T> observer);
    void notifyObservers(List<T> data);
}