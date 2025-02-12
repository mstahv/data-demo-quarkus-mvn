package org.example;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

@Entity
public class Publisher {
    @Id
    public long id;

    @NotNull
    public String name;

    @OneToMany(mappedBy = Book_.PUBLISHER)
    public Set<Book> books;
}
