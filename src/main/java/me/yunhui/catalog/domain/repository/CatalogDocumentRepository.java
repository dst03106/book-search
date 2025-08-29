package me.yunhui.catalog.domain.repository;

import me.yunhui.catalog.domain.entity.CatalogItem;
import me.yunhui.catalog.domain.vo.Pagination;
import me.yunhui.catalog.domain.vo.SearchResult;
import java.util.List;

public interface CatalogDocumentRepository {
    
    SearchResult smartSearch(String query, Pagination pagination);
    
    SearchResult orSearch(String keyword1, String keyword2, Pagination pagination);
    
    SearchResult notSearch(String includeKeyword, String excludeKeyword, Pagination pagination);
    
    void saveAll(Iterable<CatalogItem> items);
}
