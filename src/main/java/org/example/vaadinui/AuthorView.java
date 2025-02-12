package org.example.vaadinui;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
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

import static org.example.vaadinui.JakartaDataHelper.toOrder;
import static org.example.vaadinui.JakartaDataHelper.toPageRequest;

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
                // Mapping from Vaadin Grid query to Jakarta Data PageRequest/Order with helper methods
                return repo.findAll(toPageRequest(q), toOrder(q)).stream();
            });
        }
    }

}
