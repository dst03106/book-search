package me.yunhui.catalog.application;

import me.yunhui.catalog.domain.CatalogQuery;
import me.yunhui.catalog.domain.CatalogRepository;
import me.yunhui.catalog.domain.CatalogResult;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {
    
    private final CatalogRepository catalogRepository;
    
    public CatalogService(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }
    
    public CatalogResult search(CatalogQuery query) {
        return catalogRepository.search(query);
    }
}
