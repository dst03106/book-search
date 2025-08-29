package me.yunhui.catalog.domain.vo;

import me.yunhui.catalog.domain.exception.EmptyKeywordException;

public record CatalogKeyword(String value, boolean included) {
    
    public CatalogKeyword {
        if (value == null || value.trim().isEmpty()) {
            throw new EmptyKeywordException("Keyword cannot be null or empty");
        }
        value = normalizeKeyword(value);
    }
    
    public static CatalogKeyword included(String value) {
        return new CatalogKeyword(normalizeKeyword(value), true);
    }
    
    public static CatalogKeyword excluded(String value) {
        return new CatalogKeyword(normalizeKeyword(value), false);
    }
    
    public boolean isIncludedInSearch() {
        return included;
    }
    
    private static String normalizeKeyword(String value) {
        return value.toLowerCase().trim();
    }
    
    @Override
    public String toString() {
        return value;
    }
}