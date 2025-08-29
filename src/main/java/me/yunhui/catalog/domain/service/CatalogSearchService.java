package me.yunhui.catalog.domain.service;

import me.yunhui.catalog.domain.vo.CatalogQuery;
import me.yunhui.catalog.domain.vo.CatalogSearchResult;
import me.yunhui.catalog.domain.vo.Pagination;
import me.yunhui.catalog.domain.vo.ParsedQuery;
import me.yunhui.catalog.domain.vo.SearchResult;

public class CatalogSearchService {
    
    private final QueryParser queryParser;
    private final SearchStrategySelector strategySelector;
    
    public CatalogSearchService(QueryParser queryParser, SearchStrategySelector strategySelector) {
        this.queryParser = queryParser;
        this.strategySelector = strategySelector;
    }
    
    public CatalogSearchResult search(CatalogQuery catalogQuery) {
        ParsedQuery parsedQuery = queryParser.parse(catalogQuery.getQuery());
        SearchStrategy strategy = strategySelector.selectStrategy(parsedQuery);
        
        Pagination pagination = new Pagination(catalogQuery.getPage(), catalogQuery.getSize());
        SearchResult searchResult = strategy.search(parsedQuery, pagination);
        
        return CatalogSearchResult.from(searchResult, catalogQuery.getPage(), catalogQuery.getSize());
    }
}