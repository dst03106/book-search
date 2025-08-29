package me.yunhui.catalog.domain.vo;

public record Pagination(
    int page,
    int size
) {
    
    public Pagination {
        if (page < 0) {
            throw new IllegalArgumentException("Page cannot be negative");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive");
        }
    }
    
    public int offset() {
        return page * size;
    }
}
