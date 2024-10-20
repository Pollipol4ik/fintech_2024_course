package edu.java.fintechcourse2024.hw3;

import java.util.List;

public class CustomLinkedList<T> {
    private Node<T> first, last;
    private int size;

    public CustomLinkedList() {
        first = last = null;
        size = 0;
    }

    public int getSize() {
        return size;
    }

    public void add(T item) {
        if (!isEmpty()) {
            Node<T> prev = last;
            last = new Node<>(item);
            prev.next = last;
        } else {
            last = new Node<>(item);
            first = last;
        }
        size++;
    }


    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Индекс выходит за пределы списка");
        }
        Node<T> current = first;
        int currentIndex = 0;
        while (current != null) {
            if (currentIndex == index) {
                return current.value;
            }
            current = current.next;
            currentIndex++;
        }
        return null;
    }

    public void appendFirst(T item) {
        if (!isEmpty()) {
            Node<T> next = first;
            first = new Node<>(item);
            first.next = next;
        } else {
            first = new Node<>(item);
            last = first;
        }
        size++;
    }


    public void insert(T item, int location) {
        if (size <= location) {
            System.out.println("location > size");
        } else if (location == 0) {
            appendFirst(item);
            size++;
        } else {
            int index = 1;
            Node<T> now = first;
            while (now.next != null) {
                if (index == location) {
                    Node<T> newNode = new Node<>(item);
                    newNode.next = now.next;
                    now.next = newNode;
                    size++;
                    break;
                }
                index++;
                now = now.next;
            }
        }
    }


    public void remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Индекс выходит за пределы списка");
        }

        if (index == 0) {
            first = first.next;
        } else {
            Node<T> current = first;
            for (int i = 0; i < index - 1; i++) {
                current = current.next;
            }
            current.next = current.next.next;
            if (current.next == null) {
                last = current;
            }
        }
        size--;
    }


    public boolean contains(T item) {
        Node<T> current = first;
        while (current != null) {
            if (current.value.equals(item)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public void addAll(List<T> items) {
        for (T item : items) {
            add(item);
        }
    }


    public boolean isEmpty() {
        return size == 0;
    }

    public void printList() {
        Node<T> current = first;
        while (current != null) {
            System.out.print(current.value + " ");
            current = current.next;
        }
        System.out.println();
    }

}