package me.yunhui.catalog.domain.repository;

import me.yunhui.catalog.domain.entity.CatalogItem;
import me.yunhui.catalog.domain.vo.Pagination;
import me.yunhui.catalog.domain.vo.CatalogQueryResult;
import java.util.List;

public interface CatalogDocumentRepository {
    
    CatalogQueryResult smartSearch(String query, Pagination pagination);
    
    CatalogQueryResult orSearch(String keyword1, String keyword2, Pagination pagination);
    
    CatalogQueryResult notSearch(String includeKeyword, String excludeKeyword, Pagination pagination);
    
    void saveAll(Iterable<CatalogItem> items);
}
