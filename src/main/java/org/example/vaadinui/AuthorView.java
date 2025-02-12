package org.example.vaadinui;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.Route;
import jakarta.data.Order;
import jakarta.data.Sort;
import jakarta.data.page.PageRequest;
import org.example.Author;
import org.example.AuthorRepository;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.button.DeleteButton;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.grid.VGrid;
import org.vaadin.firitin.components.notification.VNotification;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.rad.AutoForm;
import org.vaadin.firitin.rad.AutoFormContext;

import java.util.List;
import java.util.function.IntFunction;

@Route(layout = Layout.class)
@MenuItem(icon = VaadinIcon.USERS)
public class AuthorView extends VerticalLayout {

    AutoFormContext autoFormContext = new AutoFormContext() {{
        getHiddenProperties().add("books"); // Edited from Books view
    }};

    public AuthorView(AuthorRepository authorRepository) {
        add(new H1("Author CRUD"));
        add("""
        Full CRUD for Author entity, based on Jakarta Data's CrudRepository.
        Note that you can only delete authors that have no books due to FK constraints.
        Also note that (especially as extending from CrudRepository) you should in real project
        probably use an actual service layer if doing anything else than just CRUD...
        """);
        var grid = new AuthorGrid(authorRepository);
        add(new VButton(VaadinIcon.PLUS.create(), e -> {
            // Utilising AutoForm from Viritin, instead of manually defining the Vaadin form
            AutoForm<Author> form = autoFormContext.createForm(new Author());
            form.setSaveHandler(author -> {
                authorRepository.insert(author);
                grid.listEntities();
            });
            form.openInDialog();
        }));
        addAndExpand(grid);
    }

    public class AuthorGrid extends VGrid<Author> {
        private final AuthorRepository repo;

        public AuthorGrid(AuthorRepository authorRepository) {
            super(Author.class);
            this.repo = authorRepository;
            listEntities();
            addComponentColumn(author -> new VHorizontalLayout(
                    new VButton(VaadinIcon.PENCIL.create(), e -> {
                        AutoForm<Author> form = autoFormContext.createForm(author);
                        form.setSaveHandler(entity -> {
                            authorRepository.save(entity);
                            listEntities();
                        });
                        form.setResetHandler(a -> listEntities());
                        form.openInDialog();
                    }),
                    new DeleteButton(() -> {
                        try {
                            authorRepository.delete(author);
                            listEntities();
                        } catch (Exception e) {
                            VNotification.prominent("Could not delete author, because ." + e.getMessage());
                        }
                    })
            ));
        }

        private void listEntities() {
            // Lazy loading of data from Jakarta Data repository with pagination to Vaadin Grid
            // New rows are fetched when scrolling (if there is more data)
            setItems(q -> {
                // TODO there could be similar helper that is shipped with Vaadin Spring
                // to do this mechanic conversion from Vaadin Query to Jakarta Data
                int page = q.getPage() + 1; // Vaadin pages are 0-based, like in Spring Data, Jakarta Data pages are 1-based
                var pageRequest = PageRequest.ofPage(page, q.getPageSize(), false);
                Order order = Order.by(q.getSortOrders().stream().map(so -> so.getDirection() == SortDirection.ASCENDING ?
                        Sort.asc(so.getSorted()) : Sort.desc(so.getSorted())).toArray((IntFunction<Sort<Author>[]>) Sort[]::new));
                List<Author> content = repo.findAll(pageRequest, order).content();
                return content.stream();
            });
        }
    }

}
