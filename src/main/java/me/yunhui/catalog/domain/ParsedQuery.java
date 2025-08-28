package me.yunhui.catalog.domain;

import me.yunhui.catalog.domain.exception.EmptyKeywordException;
import me.yunhui.catalog.domain.exception.TooManyKeywordsException;
import me.yunhui.shared.domain.ValueObject;
import java.util.List;
import java.util.Objects;

public class ParsedQuery extends ValueObject {
    
    public enum QueryType {
        SIMPLE,
        OR,
        NOT
    }
    
    private final QueryType type;
    private final List<String> keywords;
    private final String originalQuery;
    
    private ParsedQuery(QueryType type, List<String> keywords, String originalQuery) {
        if (keywords == null || keywords.isEmpty()) {
            throw new EmptyKeywordException("Keywords cannot be null or empty");
        }
        if (keywords.size() > 2) {
            throw new TooManyKeywordsException("Maximum 2 keywords are supported");
        }
        this.type = type;
        this.keywords = List.copyOf(keywords);
        this.originalQuery = originalQuery;
    }
    
    public static ParsedQuery simple(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new EmptyKeywordException("Keyword cannot be null or empty");
        }
        return new ParsedQuery(QueryType.SIMPLE, List.of(keyword.trim()), keyword);
    }
    
    public static ParsedQuery or(String keyword1, String keyword2, String originalQuery) {
        return new ParsedQuery(QueryType.OR, List.of(keyword1.trim(), keyword2.trim()), originalQuery);
    }
    
    public static ParsedQuery not(String includeKeyword, String excludeKeyword, String originalQuery) {
        return new ParsedQuery(QueryType.NOT, List.of(includeKeyword.trim(), excludeKeyword.trim()), originalQuery);
    }
    
    public static ParsedQuery parse(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new EmptyKeywordException("Query cannot be null or empty");
        }
        
        String trimmedQuery = query.trim();
        
        // OR 검색: "keyword1|keyword2"
        if (trimmedQuery.contains("|")) {
            String[] parts = trimmedQuery.split("\\|", 2);
            if (parts.length == 2) {
                return or(parts[0].trim(), parts[1].trim(), trimmedQuery);
            }
        }
        
        // NOT 검색: "keyword1-keyword2"  
        if (trimmedQuery.contains("-")) {
            String[] parts = trimmedQuery.split("-", 2);
            if (parts.length == 2) {
                return not(parts[0].trim(), parts[1].trim(), trimmedQuery);
            }
        }
        
        // 기본 단순 검색
        return simple(trimmedQuery);
    }
    
    public QueryType getType() {
        return type;
    }
    
    public List<String> getKeywords() {
        return keywords;
    }
    
    public String getOriginalQuery() {
        return originalQuery;
    }
    
    public String getFirstKeyword() {
        return keywords.get(0);
    }
    
    public String getSecondKeyword() {
        return keywords.size() > 1 ? keywords.get(1) : null;
    }
    
    public boolean isSimple() {
        return type == QueryType.SIMPLE;
    }
    
    public boolean isOr() {
        return type == QueryType.OR;
    }
    
    public boolean isNot() {
        return type == QueryType.NOT;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParsedQuery that = (ParsedQuery) o;
        return type == that.type &&
               Objects.equals(keywords, that.keywords) &&
               Objects.equals(originalQuery, that.originalQuery);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(type, keywords, originalQuery);
    }
    
    @Override
    public String toString() {
        return "ParsedQuery{" +
                "type=" + type +
                ", keywords=" + keywords +
                ", originalQuery='" + originalQuery + '\'' +
                '}';
    }
}
