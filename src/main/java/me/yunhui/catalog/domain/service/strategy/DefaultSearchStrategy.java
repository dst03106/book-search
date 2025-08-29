package me.yunhui.catalog.domain.service.strategy;

import me.yunhui.catalog.domain.repository.CatalogDocumentRepository;
import me.yunhui.catalog.domain.service.SearchStrategy;
import me.yunhui.catalog.domain.vo.Pagination;
import me.yunhui.catalog.domain.entity.CatalogParsedQuery;
import me.yunhui.catalog.domain.vo.CatalogQueryResult;

public class DefaultSearchStrategy implements SearchStrategy {
    
    private final CatalogDocumentRepository documentRepository;
    
    public DefaultSearchStrategy(CatalogDocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }
    
    @Override
    public CatalogQueryResult search(CatalogParsedQuery parsedQuery, Pagination pagination) {
        return documentRepository.smartSearch(
            parsedQuery.getFirstKeyword().value(), 
            pagination
        );
    }
    
    @Override
    public boolean supports(CatalogParsedQuery parsedQuery) {
        // 키워드가 1개이고 모두 포함 키워드인 경우 (SIMPLE)
        return parsedQuery.getKeywords().size() == 1 && 
               parsedQuery.getKeywords().get(0).isIncludedInSearch();
    }
}
