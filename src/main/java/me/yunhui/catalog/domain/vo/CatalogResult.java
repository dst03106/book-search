package me.yunhui.catalog.domain.vo;

import me.yunhui.shared.domain.ValueObject;
import java.util.List;
import java.util.Objects;

public class CatalogResult extends ValueObject {
    
    private final List<CatalogItemResult> items;
    private final long totalElements;
    private final int page;
    private final int size;

    public CatalogResult(List<CatalogItemResult> items, long totalElements, int page, int size) {
        this.items = items;
        this.totalElements = totalElements;
        this.page = page;
        this.size = size;
    }

    public List<CatalogItemResult> getItems() {
        return items;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CatalogResult that = (CatalogResult) o;
        return totalElements == that.totalElements &&
               page == that.page &&
               size == that.size &&
               Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items, totalElements, page, size);
    }

    @Override
    public String toString() {
        return "CatalogResult{" +
                "totalElements=" + totalElements +
                ", page=" + page +
                ", size=" + size +
                ", itemsCount=" + items.size() +
                '}';
    }

    public record CatalogItemResult(
        String id,
        String title,
        String subtitle,
        String author,
        String isbn,
        String publisher,
        java.time.LocalDate published,
        String imageUrl
    ) {}
}
