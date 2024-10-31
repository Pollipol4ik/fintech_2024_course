package edu.kudago.îbserver;

import java.util.List;

public interface Observer<T> {
    void update(List<T> data);
}