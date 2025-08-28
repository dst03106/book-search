package me.yunhui.catalog.domain.strategy;

import me.yunhui.catalog.domain.*;

public class NotSearchStrategy implements SearchStrategy {
    
    private final CatalogDocumentRepository documentRepository;
    
    public NotSearchStrategy(CatalogDocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }
    
    @Override
    public SearchResult search(ParsedQuery parsedQuery, Pagination pagination) {
        return documentRepository.notSearch(
            parsedQuery.getFirstKeyword(),
            parsedQuery.getSecondKeyword(),
            pagination
        );
    }
    
    @Override
    public boolean supports(ParsedQuery.QueryType queryType) {
        return queryType == ParsedQuery.QueryType.NOT;
    }
}
