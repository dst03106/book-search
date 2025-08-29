package me.yunhui.catalog.application;

import me.yunhui.catalog.domain.entity.CatalogParsedQuery;
import me.yunhui.catalog.domain.service.CatalogSearchService;
import me.yunhui.catalog.domain.service.QueryParser;
import me.yunhui.catalog.domain.vo.CatalogQuery;
import me.yunhui.catalog.domain.vo.CatalogQueryResult;
import me.yunhui.catalog.domain.vo.Pagination;
import me.yunhui.shared.domain.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {
    
    private final QueryParser queryParser;
    private final CatalogSearchService catalogSearchService;
    private final ApplicationEventPublisher eventPublisher;
    
    public CatalogService(QueryParser queryParser, CatalogSearchService catalogSearchService, ApplicationEventPublisher eventPublisher) {
        this.queryParser = queryParser;
        this.catalogSearchService = catalogSearchService;
        this.eventPublisher = eventPublisher;
    }
    
    public CatalogQueryResult search(CatalogQuery query) {
        // 쿼리 파싱 (이 시점에서 도메인 이벤트 생성)
        CatalogParsedQuery parsedQuery = queryParser.parse(query.getQuery());
        
        // 검색 실행
        Pagination pagination = new Pagination(query.getPage(), query.getSize());
        CatalogQueryResult result = catalogSearchService.search(parsedQuery, pagination);
        
        // 도메인 이벤트 발행
        for (DomainEvent event : parsedQuery.getUncommittedEvents()) {
            eventPublisher.publishEvent(event);
        }
        parsedQuery.markEventsAsCommitted();
        
        return result;
    }
}

