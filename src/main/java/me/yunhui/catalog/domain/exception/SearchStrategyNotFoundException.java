package me.yunhui.catalog.domain.exception;

import me.yunhui.catalog.domain.entity.CatalogParsedQuery;

public class SearchStrategyNotFoundException extends CatalogDomainException {
    
    private static final String ERROR_CODE = "CATALOG_004";
    
    public SearchStrategyNotFoundException(CatalogParsedQuery parsedQuery) {
        super("No search strategy found for query: " + parsedQuery, ERROR_CODE);
    }
}
