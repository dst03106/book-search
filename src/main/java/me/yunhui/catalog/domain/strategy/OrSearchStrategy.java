package me.yunhui.catalog.domain.strategy;

import me.yunhui.catalog.domain.*;

public class OrSearchStrategy implements SearchStrategy {
    
    private final CatalogDocumentRepository documentRepository;
    
    public OrSearchStrategy(CatalogDocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }
    
    @Override
    public SearchResult search(ParsedQuery parsedQuery, Pagination pagination) {
        return documentRepository.orSearch(
            parsedQuery.getFirstKeyword(),
            parsedQuery.getSecondKeyword(),
            pagination
        );
    }
    
    @Override
    public boolean supports(ParsedQuery.QueryType queryType) {
        return queryType == ParsedQuery.QueryType.OR;
    }
}
