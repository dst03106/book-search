package me.yunhui.catalog.domain.entity;

import me.yunhui.catalog.domain.event.CatalogKeywordSearchedEvent;
import me.yunhui.catalog.domain.exception.EmptyKeywordException;
import me.yunhui.catalog.domain.exception.TooManyKeywordsException;
import me.yunhui.catalog.domain.vo.CatalogKeyword;
import me.yunhui.shared.domain.AggregateRoot;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CatalogParsedQuery extends AggregateRoot<String> {
    
    private final String id;
    private final List<CatalogKeyword> keywords;
    private final String originalQuery;
    
    private CatalogParsedQuery(List<CatalogKeyword> keywords, String originalQuery) {
        if (keywords == null || keywords.isEmpty()) {
            throw new EmptyKeywordException("Keywords cannot be null or empty");
        }
        if (keywords.size() > 2) {
            throw new TooManyKeywordsException("Maximum 2 keywords are supported");
        }
        this.id = UUID.randomUUID().toString();
        this.keywords = List.copyOf(keywords);
        this.originalQuery = originalQuery;
        
        // 포함될 키워드들에 대한 이벤트 발행
        List<CatalogKeyword> includedKeywords = keywords.stream()
                .filter(CatalogKeyword::isIncludedInSearch)
                .toList();
        
        if (!includedKeywords.isEmpty()) {
            addEvent(CatalogKeywordSearchedEvent.of(includedKeywords));
        }
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    public static CatalogParsedQuery simple(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new EmptyKeywordException("Keyword cannot be null or empty");
        }
        return new CatalogParsedQuery(List.of(CatalogKeyword.included(keyword)), keyword);
    }
    
    public static CatalogParsedQuery or(String keyword1, String keyword2, String originalQuery) {
        return new CatalogParsedQuery(List.of(CatalogKeyword.included(keyword1), CatalogKeyword.included(keyword2)), originalQuery);
    }
    
    public static CatalogParsedQuery not(String includeKeyword, String excludeKeyword, String originalQuery) {
        return new CatalogParsedQuery(List.of(CatalogKeyword.included(includeKeyword), CatalogKeyword.excluded(excludeKeyword)), originalQuery);
    }
    
    
    public List<CatalogKeyword> getKeywords() {
        return keywords;
    }
    
    public String getOriginalQuery() {
        return originalQuery;
    }
    
    public CatalogKeyword getFirstKeyword() {
        return keywords.get(0);
    }
    
    public CatalogKeyword getSecondKeyword() {
        return keywords.size() > 1 ? keywords.get(1) : null;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CatalogParsedQuery that = (CatalogParsedQuery) o;
        return Objects.equals(keywords, that.keywords) &&
               Objects.equals(originalQuery, that.originalQuery);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(keywords, originalQuery);
    }
    
    @Override
    public String toString() {
        return "CatalogParsedQuery{" +
                "keywords=" + keywords +
                ", originalQuery='" + originalQuery + '\'' +
                '}';
    }
}
