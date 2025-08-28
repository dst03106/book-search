package me.yunhui.catalog.application;

import me.yunhui.catalog.domain.*;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {
    
    private final CatalogSearchService catalogSearchService;
    
    public CatalogService(CatalogSearchService catalogSearchService) {
        this.catalogSearchService = catalogSearchService;
    }
    
    public CatalogResult search(CatalogQuery query) {
        CatalogSearchResult searchResult = catalogSearchService.search(query);
        
        return new CatalogResult(
            searchResult.items().stream()
                .map(this::toResultItem)
                .toList(),
            searchResult.totalCount(),
            searchResult.page(),
            searchResult.size()
        );
    }
    
    private CatalogResult.CatalogItemResult toResultItem(CatalogSearchResult.CatalogResultItem item) {
        return new CatalogResult.CatalogItemResult(
            item.id(),
            item.title(),
            item.subtitle(),
            item.author(),
            item.isbn(),
            item.publisher(),
            item.published() != null ? java.time.LocalDate.parse(item.published()) : null,
            item.imageUrl()
        );
    }
}

