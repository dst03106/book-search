package me.yunhui.catalog.domain;

public interface CatalogRepository {
    
    CatalogResult search(CatalogQuery query);
    
    void saveAll(Iterable<CatalogItem> items);
}
