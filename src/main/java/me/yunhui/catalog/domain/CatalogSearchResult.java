package me.yunhui.catalog.domain;

import java.util.List;

public record CatalogSearchResult(
    List<CatalogResultItem> items,
    long totalCount,
    int page,
    int size
) {
    
    public record CatalogResultItem(
        String id,
        String title,
        String subtitle,
        String author,
        String isbn,
        String publisher,
        String published,
        String imageUrl
    ) {}
    
    public static CatalogSearchResult from(SearchResult searchResult, int page, int size) {
        List<CatalogResultItem> resultItems = searchResult.items().stream()
            .map(item -> new CatalogResultItem(
                item.id(),
                item.title(),
                item.subtitle(),
                item.author(),
                item.isbn(),
                item.publisher(),
                item.published() != null ? item.published().toString() : null,
                item.imageUrl()
            ))
            .toList();
            
        return new CatalogSearchResult(
            resultItems,
            searchResult.totalCount(),
            page,
            size
        );
    }
}