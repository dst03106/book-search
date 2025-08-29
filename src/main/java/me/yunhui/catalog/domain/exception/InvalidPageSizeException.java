package me.yunhui.catalog.domain.exception;

public class InvalidPageSizeException extends CatalogDomainException {
    
    private static final String ERROR_CODE = "CATALOG_006";
    
    public InvalidPageSizeException(String message) {
        super(message, ERROR_CODE);
    }
}