package org.example;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.hibernate.StatelessSession;

import java.util.List;

@ApplicationScoped
public class LibraryResource {

    @Inject Library library;
    @Inject EntityManager em;
    @Inject
    StatelessSession session;

    public Book byIsbn(String isbn) {
        return library.byIsbn(isbn)
                .orElseThrow(() ->new RuntimeException("Not found" + isbn));
    }

    public void loadAuthors(Book book) {
        session.fetch(book.authors);
    }

    public List<Book> byTitle(String title) {
        String pattern = '%' + title.replace('*', '%') + '%';
        return library.byTitle(pattern);
    }

    public  List<Book> allBooks() {
        return library.allBooks(_Book.title.ascIgnoreCase());
    }

    public String create(Book book) {
        library.add(book);
        return "Added " + book.isbn;
    }

    @Transactional
    public String delete(String isbn) {
        library.delete(isbn);
        return "Deleted " + isbn;
    }

    public  List<Summary> summary() {
        return library.summarize();
    }

    public List<Publisher> allPublishers() {
        return library.allPublishers();
    }

    public List<Author> allAuthors() {
        return library.allAuthors();
    }
}
