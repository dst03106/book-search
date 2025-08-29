package me.yunhui.catalog.domain.service;

import me.yunhui.catalog.domain.vo.Pagination;
import me.yunhui.catalog.domain.entity.CatalogParsedQuery;
import me.yunhui.catalog.domain.vo.CatalogQueryResult;

public class CatalogSearchService {
    
    private final SearchStrategySelector strategySelector;
    
    public CatalogSearchService(SearchStrategySelector strategySelector) {
        this.strategySelector = strategySelector;
    }
    
    public CatalogQueryResult search(CatalogParsedQuery parsedQuery, Pagination pagination) {
        SearchStrategy strategy = strategySelector.selectStrategy(parsedQuery);
        return strategy.search(parsedQuery, pagination);
    }
}