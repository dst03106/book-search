package me.yunhui.catalog.infrastructure.config;

import me.yunhui.catalog.domain.handler.CatalogKeywordTrackingHandler;
import me.yunhui.catalog.domain.repository.CatalogDocumentRepository;
import me.yunhui.catalog.domain.repository.CatalogKeywordRepository;
import me.yunhui.catalog.domain.service.CatalogSearchService;
import me.yunhui.catalog.domain.service.QueryParser;
import me.yunhui.catalog.domain.service.SearchStrategy;
import me.yunhui.catalog.domain.service.SearchStrategySelector;
import me.yunhui.catalog.domain.service.strategy.DefaultSearchStrategy;
import me.yunhui.catalog.domain.service.strategy.NotSearchStrategy;
import me.yunhui.catalog.domain.service.strategy.OrSearchStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CatalogDomainConfiguration {
    
    @Bean
    public DefaultSearchStrategy defaultSearchStrategy(CatalogDocumentRepository catalogDocumentRepository) {
        return new DefaultSearchStrategy(catalogDocumentRepository);
    }
    
    @Bean
    public OrSearchStrategy orSearchStrategy(CatalogDocumentRepository catalogDocumentRepository) {
        return new OrSearchStrategy(catalogDocumentRepository);
    }
    
    @Bean
    public NotSearchStrategy notSearchStrategy(CatalogDocumentRepository catalogDocumentRepository) {
        return new NotSearchStrategy(catalogDocumentRepository);
    }
    
    @Bean
    public SearchStrategySelector searchStrategySelector(List<SearchStrategy> strategies) {
        return new SearchStrategySelector(strategies);
    }
    
    @Bean
    public QueryParser queryParser() {
        return new QueryParser();
    }
    
    @Bean
    public CatalogSearchService catalogSearchService(SearchStrategySelector searchStrategySelector) {
        return new CatalogSearchService(searchStrategySelector);
    }
    
    @Bean
    public CatalogKeywordTrackingHandler catalogKeywordTrackingHandler(CatalogKeywordRepository catalogKeywordRepository) {
        return new CatalogKeywordTrackingHandler(catalogKeywordRepository);
    }
}
