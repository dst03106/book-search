package me.yunhui.catalog.domain;

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