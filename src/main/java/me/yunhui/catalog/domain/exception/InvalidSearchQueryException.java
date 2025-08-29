package me.yunhui.catalog.domain.exception;

public class InvalidSearchQueryException extends CatalogDomainException {
    
    private static final String ERROR_CODE = "CATALOG_004";
    
    public InvalidSearchQueryException(String message) {
        super(message, ERROR_CODE);
    }
}