package me.yunhui.catalog.domain.event;

import me.yunhui.catalog.domain.vo.CatalogKeyword;
import me.yunhui.shared.domain.DomainEvent;

import java.time.LocalDateTime;
import java.util.List;

public record CatalogKeywordSearchedEvent(
    List<CatalogKeyword> searchedKeywords,
    LocalDateTime occurredOn
) implements DomainEvent {
    
    public static CatalogKeywordSearchedEvent of(List<CatalogKeyword> keywords) {
        return new CatalogKeywordSearchedEvent(keywords, LocalDateTime.now());
    }
}