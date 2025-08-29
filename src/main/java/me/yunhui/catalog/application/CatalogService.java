package me.yunhui.catalog.application;

import me.yunhui.catalog.domain.service.CatalogSearchService;
import me.yunhui.catalog.domain.vo.CatalogQuery;
import me.yunhui.catalog.domain.vo.CatalogQueryResult;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {
    
    private final CatalogSearchService catalogSearchService;
    
    public CatalogService(CatalogSearchService catalogSearchService) {
        this.catalogSearchService = catalogSearchService;
    }
    
    public CatalogQueryResult search(CatalogQuery query) {
        return catalogSearchService.search(query);
    }
}

