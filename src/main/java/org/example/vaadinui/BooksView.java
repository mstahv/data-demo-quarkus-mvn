package org.example.vaadinui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.example.Author;
import org.example.Book;
import org.example.LibraryResource;
import org.example.Publisher;
import org.example.Type;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.RichText;
import org.vaadin.firitin.components.button.DefaultButton;
import org.vaadin.firitin.components.combobox.VComboBox;
import org.vaadin.firitin.components.textfield.VTextField;
import org.vaadin.firitin.fields.EnumSelect;
import org.vaadin.firitin.form.BeanValidationForm;

import java.util.List;

import org.vaadin.firitin.components.datepicker.VDatePicker;
import org.vaadin.firitin.components.textfield.VIntegerField;
import org.vaadin.firitin.rad.PrettyPrinter;

@Route(layout = Layout.class)
@MenuItem(icon = VaadinIcon.BOOK)
public class BooksView extends VerticalLayout {
    public BooksView(LibraryResource libraryResource) {
        add(new RichText().withMarkDown("""
                ## Books
                
                This is a trivial view for books based on the LibraryResource (changed from
                 REST endpoint to "regular service class" from the original Jakarta Data example.
                """));

        BookGrid bookGrid = new BookGrid(libraryResource);

        add(new DefaultButton("New Book", e -> {
            var form = new BookForm(libraryResource);
            form.setEntity(new Book());
            form.setSavedHandler(book -> {
                libraryResource.create(book);
                form.closePopup();
                bookGrid.listBooks();
            });
            form.openInModalPopup();
            form.getPopup().setHeaderTitle("New Book");
        }));

        addAndExpand(bookGrid);

    }

    public static class BookForm extends BeanValidationForm<Book> {

        TextField isbn = new VTextField("ISBN");
        TextField title = new VTextField("Title");
        TextField text = new VTextField("Text");
        DatePicker publicationDate = new VDatePicker("Publication Date");
        IntegerField pages = new VIntegerField("Pages");
        EnumSelect<Type> type = new EnumSelect<>("Type", Type.class);
        VComboBox<Publisher> publisher = new VComboBox<>("Publisher"){{
            setItemLabelGenerator(p -> p.name);
        }};
        MultiSelectComboBox<Author> authors = new MultiSelectComboBox<>("Authors") {{
            setItemLabelGenerator(a -> a.name);
        }};
        BigDecimalField price = new BigDecimalField("Price");
        // Not a native field type, but Binder will apply a converter
        TextField quantitySold = new VTextField("Quantity Sold");

        public BookForm(LibraryResource libraryResource) {
            super(Book.class);
            // Note, if database was big these should be lazy loaded bindings
            publisher.setItems(libraryResource.allPublishers());
            authors.setItems(libraryResource.allAuthors());
        }

        @Override
        protected List<Component> getFormComponents() {
            return List.of(isbn, title, text, pages, publicationDate, type, publisher, authors, price, quantitySold);
        }

    }

    private static class BookGrid extends Grid<Book> {
        private final LibraryResource libraryResource;

        public BookGrid(LibraryResource libraryResource) {
            super(Book.class);
            setColumns("isbn", "title", "publicationDate", "pages", "type");
            this.libraryResource = libraryResource;
            listBooks();
            asSingleSelect().addValueChangeListener(e -> {
                var book = e.getValue();
                if (book != null) {
                    // Load authors for the detail dialog (lazy loaded set)
                    libraryResource.loadAuthors(book);
                    new Dialog("Book details", PrettyPrinter.toVaadin(book)).open();
                }
                e.getSource().deselectAll();
            });
        }

        void listBooks() {
            setItems(libraryResource.allBooks());
        }
    }
}
