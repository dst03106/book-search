package me.yunhui.catalog.domain.service.strategy;

import me.yunhui.catalog.domain.repository.CatalogDocumentRepository;
import me.yunhui.catalog.domain.service.SearchStrategy;
import me.yunhui.catalog.domain.vo.Pagination;
import me.yunhui.catalog.domain.entity.CatalogParsedQuery;
import me.yunhui.catalog.domain.vo.CatalogQueryResult;

public class NotSearchStrategy implements SearchStrategy {
    
    private final CatalogDocumentRepository documentRepository;
    
    public NotSearchStrategy(CatalogDocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }
    
    @Override
    public CatalogQueryResult search(CatalogParsedQuery parsedQuery, Pagination pagination) {
        return documentRepository.notSearch(
            parsedQuery.getFirstKeyword().value(),
            parsedQuery.getSecondKeyword().value(),
            pagination
        );
    }
    
    @Override
    public boolean supports(CatalogParsedQuery parsedQuery) {
        // 키워드가 2개이고 하나는 포함, 하나는 제외 키워드인 경우 (NOT)
        return parsedQuery.getKeywords().size() == 2 && 
               parsedQuery.getKeywords().stream().anyMatch(k -> !k.isIncludedInSearch());
    }
}
