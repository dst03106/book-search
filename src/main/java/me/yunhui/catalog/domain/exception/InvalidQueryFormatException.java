package me.yunhui.catalog.domain.exception;

public class InvalidQueryFormatException extends CatalogDomainException {
    
    private static final String ERROR_CODE = "CATALOG_003";
    
    public InvalidQueryFormatException(String message) {
        super(message, ERROR_CODE);
    }
}