package edu.java.fintechcourse2024.hw3;

import java.util.function.Consumer;

public class CustomIterator<T> {
    private Node<T> current;

    public CustomIterator(Node<T> first) {
        this.current = first;
    }

    public boolean hasNext() {
        return current != null;
    }

    public T next() {
        if (!hasNext()) {
            throw new java.util.NoSuchElementException("Элементов больше нет");
        }
        T value = current.value;
        current = current.next;
        return value;
    }

    public void forEachRemaining(Consumer<? super T> action) {
        while (hasNext()) {
            action.accept(next());
        }
    }
}
