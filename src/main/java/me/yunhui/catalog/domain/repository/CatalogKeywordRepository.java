package me.yunhui.catalog.domain.repository;

import me.yunhui.catalog.domain.vo.CatalogKeyword;

import java.util.List;

public interface CatalogKeywordRepository {
    
    void incrementSearchCount(CatalogKeyword keyword);
    
    List<CatalogKeyword> findTopPopularKeywords(int limit);
}