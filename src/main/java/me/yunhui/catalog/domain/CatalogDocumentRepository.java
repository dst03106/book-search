package me.yunhui.catalog.domain;

import java.util.List;

public interface CatalogDocumentRepository {
    
    SearchResult smartSearch(String query, Pagination pagination);
    
    SearchResult orSearch(String keyword1, String keyword2, Pagination pagination);
    
    SearchResult notSearch(String includeKeyword, String excludeKeyword, Pagination pagination);
    
    void saveAll(Iterable<CatalogItem> items);
}
