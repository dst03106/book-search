package me.yunhui.catalog.domain.strategy;

import me.yunhui.catalog.domain.repository.CatalogDocumentRepository;
import me.yunhui.catalog.domain.service.SearchStrategy;
import me.yunhui.catalog.domain.vo.Pagination;
import me.yunhui.catalog.domain.vo.ParsedQuery;
import me.yunhui.catalog.domain.vo.CatalogQueryResult;

public class NotSearchStrategy implements SearchStrategy {
    
    private final CatalogDocumentRepository documentRepository;
    
    public NotSearchStrategy(CatalogDocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }
    
    @Override
    public CatalogQueryResult search(ParsedQuery parsedQuery, Pagination pagination) {
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
