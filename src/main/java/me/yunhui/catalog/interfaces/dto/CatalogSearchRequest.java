package me.yunhui.catalog.interfaces.dto;

public record CatalogSearchRequest(
    String q,
    Integer page,
    Integer size
) {
    public CatalogSearchRequest {
        if (q == null || q.isBlank()) {
            throw new IllegalArgumentException("Query parameter 'q' is required");
        }
        if (page == null || page < 0) {
            page = 0;
        }
        if (size == null || size <= 0 || size > 100) {
            size = 10;
        }
    }
    
    public int getPageOrDefault() {
        return page != null ? page : 0;
    }
    
    public int getSizeOrDefault() {
        return size != null ? size : 10;
    }
}
