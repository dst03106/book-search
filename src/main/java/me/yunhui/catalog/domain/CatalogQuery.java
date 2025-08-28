package me.yunhui.catalog.domain;

import me.yunhui.shared.domain.ValueObject;
import java.util.Objects;

public class CatalogQuery extends ValueObject {
    
    private final String query;
    private final int page;
    private final int size;

    private CatalogQuery(String query, int page, int size) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Query cannot be null or blank");
        }
        if (page < 0) {
            throw new IllegalArgumentException("Page cannot be negative");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive");
        }
        this.query = query.trim();
        this.page = page;
        this.size = size;
    }

    public static CatalogQuery of(String query, int page, int size) {
        return new CatalogQuery(query, page, size);
    }

    public String getQuery() {
        return query;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CatalogQuery that = (CatalogQuery) o;
        return page == that.page &&
               size == that.size &&
               Objects.equals(query, that.query);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, page, size);
    }

    @Override
    public String toString() {
        return "CatalogQuery{" +
                "query='" + query + '\'' +
                ", page=" + page +
                ", size=" + size +
                '}';
    }
}
