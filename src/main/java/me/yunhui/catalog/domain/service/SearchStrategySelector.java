package me.yunhui.catalog.domain.service;

import me.yunhui.catalog.domain.exception.SearchStrategyNotFoundException;
import me.yunhui.catalog.domain.vo.ParsedQuery;

import java.util.List;

public class SearchStrategySelector {
    
    private final List<SearchStrategy> strategies;
    
    public SearchStrategySelector(List<SearchStrategy> strategies) {
        this.strategies = strategies;
    }
    
    public SearchStrategy selectStrategy(ParsedQuery parsedQuery) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(parsedQuery.getType()))
                .findFirst()
                .orElseThrow(() -> new SearchStrategyNotFoundException(parsedQuery.getType()));
    }
}
