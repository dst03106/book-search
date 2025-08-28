package me.yunhui.catalog.domain.strategy;

import me.yunhui.catalog.domain.*;

public class DefaultSearchStrategy implements SearchStrategy {
    
    private final CatalogDocumentRepository documentRepository;
    
    public DefaultSearchStrategy(CatalogDocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }
    
    @Override
    public SearchResult search(ParsedQuery parsedQuery, Pagination pagination) {
        return documentRepository.smartSearch(
            parsedQuery.getFirstKeyword(), 
            pagination
        );
    }
    
    @Override
    public boolean supports(ParsedQuery.QueryType queryType) {
        return queryType == ParsedQuery.QueryType.SIMPLE;
    }
}
