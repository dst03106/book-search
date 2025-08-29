package me.yunhui.catalog.domain.handler;

import me.yunhui.catalog.domain.event.CatalogKeywordSearchedEvent;
import me.yunhui.catalog.domain.repository.CatalogKeywordRepository;

public class CatalogKeywordTrackingHandler {
    
    private final CatalogKeywordRepository catalogKeywordRepository;
    
    public CatalogKeywordTrackingHandler(CatalogKeywordRepository catalogKeywordRepository) {
        this.catalogKeywordRepository = catalogKeywordRepository;
    }
    
    public void handle(CatalogKeywordSearchedEvent event) {
        event.searchedKeywords().forEach(catalogKeywordRepository::incrementSearchCount);
    }
}