package me.yunhui.catalog.domain.exception;

import me.yunhui.catalog.domain.ParsedQuery;

public class SearchStrategyNotFoundException extends CatalogDomainException {
    
    private static final String ERROR_CODE = "CATALOG_004";
    
    public SearchStrategyNotFoundException(ParsedQuery.QueryType queryType) {
        super("No search strategy found for query type: " + queryType, ERROR_CODE);
    }
}
