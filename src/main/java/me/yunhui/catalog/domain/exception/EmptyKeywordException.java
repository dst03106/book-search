package me.yunhui.catalog.domain.exception;

public class EmptyKeywordException extends CatalogDomainException {
    
    private static final String ERROR_CODE = "CATALOG_001";
    
    public EmptyKeywordException(String message) {
        super(message, ERROR_CODE);
    }
}