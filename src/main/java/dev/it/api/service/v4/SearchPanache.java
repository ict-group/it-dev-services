package dev.it.api.service.v4;

import io.quarkus.hibernate.orm.panache.PanacheQuery;

public interface SearchPanache<T> {
    PanacheQuery<T> get(String orderBy);
}
