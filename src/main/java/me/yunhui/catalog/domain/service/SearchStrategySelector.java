package me.yunhui.catalog.domain.service;

import me.yunhui.catalog.domain.exception.SearchStrategyNotFoundException;
import me.yunhui.catalog.domain.vo.CatalogParsedQuery;

import java.util.List;

public class SearchStrategySelector {
    
    private final List<SearchStrategy> strategies;
    
    public SearchStrategySelector(List<SearchStrategy> strategies) {
        this.strategies = strategies;
    }
    
    public SearchStrategy selectStrategy(CatalogParsedQuery catalogParsedQuery) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(catalogParsedQuery))
                .findFirst()
                .orElseThrow(() -> new SearchStrategyNotFoundException(catalogParsedQuery));
    }
}
