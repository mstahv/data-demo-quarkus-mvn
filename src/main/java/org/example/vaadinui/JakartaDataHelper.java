package org.example.vaadinui;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;
import jakarta.data.Order;
import jakarta.data.Sort;
import jakarta.data.page.PageRequest;

import java.util.function.IntFunction;

/**
 * Provides helper methods for converting Vaadin Query to similar Jakarta Data components.
 * Bit like VaadinSpringDataHelpers in Vaadin Spring.
 */
public class JakartaDataHelper {

    public static PageRequest toPageRequest(Query<?, ?> query) {
        int page = query.getPage() + 1; // Vaadin pages are 0-based, like in Spring Data, Jakarta Data pages are 1-based
        return PageRequest.ofPage(page, query.getPageSize(), false);
    }

    public static <T> Order<T> toOrder(Query<T,?> query) {
        return Order.by(query.getSortOrders().stream().map(so -> so.getDirection() == SortDirection.ASCENDING ?
                Sort.asc(so.getSorted()) : Sort.desc(so.getSorted())).toArray((IntFunction<Sort<T>[]>) Sort[]::new));
    }
}
