package me.yunhui.catalog.domain.service.strategy;

import me.yunhui.catalog.domain.repository.CatalogDocumentRepository;
import me.yunhui.catalog.domain.service.SearchStrategy;
import me.yunhui.catalog.domain.vo.Pagination;
import me.yunhui.catalog.domain.vo.CatalogParsedQuery;
import me.yunhui.catalog.domain.vo.CatalogQueryResult;

public class OrSearchStrategy implements SearchStrategy {
    
    private final CatalogDocumentRepository documentRepository;
    
    public OrSearchStrategy(CatalogDocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }
    
    @Override
    public CatalogQueryResult search(CatalogParsedQuery parsedQuery, Pagination pagination) {
        return documentRepository.orSearch(
            parsedQuery.getFirstKeyword(),
            parsedQuery.getSecondKeyword(),
            pagination
        );
    }
    
    @Override
    public boolean supports(CatalogParsedQuery.QueryType queryType) {
        return queryType == CatalogParsedQuery.QueryType.OR;
    }
}
