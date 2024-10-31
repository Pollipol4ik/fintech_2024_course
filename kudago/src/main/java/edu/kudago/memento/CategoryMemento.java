package edu.kudago.memento;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CategoryMemento implements Serializable {
    private Integer id;
    private String name;
    private String slug;
    private LocalDateTime timestamp;

    public CategoryMemento(Integer id, String name, String slug) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.timestamp = LocalDateTime.now();
    }
}
