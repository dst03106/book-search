package me.yunhui.catalog.domain.strategy;

import me.yunhui.catalog.domain.repository.CatalogDocumentRepository;
import me.yunhui.catalog.domain.service.SearchStrategy;
import me.yunhui.catalog.domain.vo.Pagination;
import me.yunhui.catalog.domain.vo.ParsedQuery;
import me.yunhui.catalog.domain.vo.CatalogQueryResult;

public class DefaultSearchStrategy implements SearchStrategy {
    
    private final CatalogDocumentRepository documentRepository;
    
    public DefaultSearchStrategy(CatalogDocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }
    
    @Override
    public CatalogQueryResult search(ParsedQuery parsedQuery, Pagination pagination) {
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
