package edu.java.fintechcourse2024.hw3;

public class Node<T>{
    public T value;
    public Node<T> next;
    public Node(T value){
        this.value = value;
        next = null;
    }
}