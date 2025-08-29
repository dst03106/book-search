package me.yunhui.catalog.domain.service;

import me.yunhui.catalog.domain.vo.CatalogQuery;
import me.yunhui.catalog.domain.vo.Pagination;
import me.yunhui.catalog.domain.vo.CatalogParsedQuery;
import me.yunhui.catalog.domain.vo.CatalogQueryResult;

public class CatalogSearchService {
    
    private final QueryParser queryParser;
    private final SearchStrategySelector strategySelector;
    
    public CatalogSearchService(QueryParser queryParser, SearchStrategySelector strategySelector) {
        this.queryParser = queryParser;
        this.strategySelector = strategySelector;
    }
    
    public CatalogQueryResult search(CatalogQuery catalogQuery) {
        CatalogParsedQuery parsedQuery = queryParser.parse(catalogQuery.getQuery());
        SearchStrategy strategy = strategySelector.selectStrategy(parsedQuery);
        
        Pagination pagination = new Pagination(catalogQuery.getPage(), catalogQuery.getSize());
        return strategy.search(parsedQuery, pagination);
    }
}