package me.yunhui.catalog.domain.exception;

public class InvalidPageNumberException extends CatalogDomainException {
    
    private static final String ERROR_CODE = "CATALOG_005";
    
    public InvalidPageNumberException(String message) {
        super(message, ERROR_CODE);
    }
}