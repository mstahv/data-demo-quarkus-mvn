# Jakarta Data & Vaadin integration

This project is a simple example of how to integrate Jakarta/Hibernate Data with Vaadin. It is
based on the [Hibernate Data example by Gavin King](https://github.com/gavinking/data-demo-quarkus-mvn), but instead of exposing REST services, it exposes a Vaadin
UI.

The project uses Quarkus. If you are Quarkus newbie, run it with:

```shell
./mvnw quarkus:dev
```

The web app then runs on http://localhost:8080. Note, Vaadin dev mode is disabled, but Quarkus dev mode will reload your changes anyway if you reload the browser manually.

Other notable changes (in addition to the Vaadin web GUI) to the original example:

 * book-author relationship is mapped from book (instead of author), easier to add new books (which I implemented first in the Vaadin UI) as authors don't need to be updated separately.
 * There is AuthorRepository extending CrudRepository, which provides Spring Data JPA like default methods suitable for basic CRUD operations. This is utilized (in an ugly manner) directly from the AuthorView. This view is utilizing Viritin's RAD helpers, but building a similar forms with Vaadin's core components would be pretty similar (but with Binder you might e.g. need getters and setters etc.).
 * Some relations were made public (don't know why they were package private).
