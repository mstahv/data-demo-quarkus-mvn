package org.example.vaadinui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import org.example.LibraryResource;
import org.example.Summary;
import org.vaadin.firitin.appframework.MenuItem;

@Route(layout = Layout.class)
@MenuItem(order = MenuItem.BEGINNING)
public class MainView extends VerticalLayout {
    public MainView(LibraryResource libraryResource) {
        add(new H1("📚 Library Summary"));
        add("This is a trivial Vaadin UI example, accessing DB via Jakarta/Hibernate Data. Here simply listing Summary aggregate via LibraryResource.");
        add(new Grid(Summary.class){{
            setItems(libraryResource.summary());
        }});

        add(new RouterLink("List and add new Books", BooksView.class));

        add(new Anchor("https://github.com/mstahv/jakarta-data-example", "Full Source Code in GitHub"));

    }

}
