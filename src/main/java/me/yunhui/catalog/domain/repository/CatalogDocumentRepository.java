package me.yunhui.catalog.domain.repository;

import me.yunhui.catalog.domain.entity.CatalogItem;
import me.yunhui.catalog.domain.vo.Pagination;
import me.yunhui.catalog.domain.vo.CatalogQueryResult;
import java.util.List;

public interface CatalogDocumentRepository {
    
    CatalogQueryResult smartSearch(String query, Pagination pagination);
    
    CatalogQueryResult orSearch(List<String> keywords, Pagination pagination);

    CatalogQueryResult notSearch(List<String> includeKeywords, List<String> excludeKeywords, Pagination pagination);
    
    void saveAll(Iterable<CatalogItem> items);
}
