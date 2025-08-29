package me.yunhui.catalog.domain.exception;

import me.yunhui.catalog.domain.vo.CatalogParsedQuery;

public class SearchStrategyNotFoundException extends CatalogDomainException {
    
    private static final String ERROR_CODE = "CATALOG_004";
    
    public SearchStrategyNotFoundException(CatalogParsedQuery.QueryType queryType) {
        super("No search strategy found for query type: " + queryType, ERROR_CODE);
    }
}
