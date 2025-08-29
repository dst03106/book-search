package me.yunhui.catalog.domain.service;

import me.yunhui.catalog.domain.exception.EmptyKeywordException;
import me.yunhui.catalog.domain.exception.InvalidQueryFormatException;
import me.yunhui.catalog.domain.vo.ParsedQuery;

public class QueryParser {
    
    public ParsedQuery parse(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new EmptyKeywordException("Query cannot be null or empty");
        }
        
        String trimmedQuery = query.trim();
        
        // OR 검색: "keyword1|keyword2"
        if (trimmedQuery.contains("|")) {
            return parseOrQuery(trimmedQuery);
        }
        
        // NOT 검색: "keyword1-keyword2"  
        if (trimmedQuery.contains("-")) {
            return parseNotQuery(trimmedQuery);
        }
        
        // 기본 단순 검색
        return ParsedQuery.simple(trimmedQuery);
    }
    
    private ParsedQuery parseOrQuery(String queryString) {
        String[] parts = queryString.split("\\|", 2);
        if (parts.length != 2) {
            throw new InvalidQueryFormatException("Invalid OR query format. Expected: keyword1|keyword2");
        }
        
        String keyword1 = parts[0].trim();
        String keyword2 = parts[1].trim();
        
        if (keyword1.isEmpty() || keyword2.isEmpty()) {
            throw new EmptyKeywordException("OR query keywords cannot be empty");
        }
        
        return ParsedQuery.or(keyword1, keyword2, queryString);
    }
    
    private ParsedQuery parseNotQuery(String queryString) {
        String[] parts = queryString.split("-", 2);
        if (parts.length != 2) {
            throw new InvalidQueryFormatException("Invalid NOT query format. Expected: keyword1-keyword2");
        }
        
        String includeKeyword = parts[0].trim();
        String excludeKeyword = parts[1].trim();
        
        if (includeKeyword.isEmpty() || excludeKeyword.isEmpty()) {
            throw new EmptyKeywordException("NOT query keywords cannot be empty");
        }
        
        return ParsedQuery.not(includeKeyword, excludeKeyword, queryString);
    }
}