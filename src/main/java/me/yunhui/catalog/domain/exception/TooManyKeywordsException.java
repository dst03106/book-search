package me.yunhui.catalog.domain.exception;

public class TooManyKeywordsException extends CatalogDomainException {
    
    private static final String ERROR_CODE = "CATALOG_002";
    
    public TooManyKeywordsException(String message) {
        super(message, ERROR_CODE);
    }
}