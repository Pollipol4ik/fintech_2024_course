package edu.kudago.memento;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

@Getter
public class HistoryService<T, M> {
    private final LinkedList<M> history = new LinkedList<>();
    private final Function<T, M> mementoCreator;

    public HistoryService(Function<T, M> mementoCreator) {
        this.mementoCreator = mementoCreator;
    }

    public void saveMemento(T entity) {
        M newMemento = mementoCreator.apply(entity);
        if (history.isEmpty() || !history.getLast().equals(newMemento)) {
            history.addLast(newMemento);
        }
    }

    public M getLastMemento() {
        return history.isEmpty() ? null : history.getLast();
    }

    public M getPreviousMemento() {
        return history.size() < 2 ? null : history.get(history.size() - 2);
    }

    public List<M> getHistory() {
        return new LinkedList<>(history);
    }
}
