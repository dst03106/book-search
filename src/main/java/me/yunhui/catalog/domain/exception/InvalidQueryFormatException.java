package me.yunhui.catalog.domain.exception;

public class InvalidQueryFormatException extends CatalogDomainException {
    
    public InvalidQueryFormatException(String message) {
        super(message);
    }
}