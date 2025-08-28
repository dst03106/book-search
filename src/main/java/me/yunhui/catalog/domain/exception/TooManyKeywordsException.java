package me.yunhui.catalog.domain.exception;

public class TooManyKeywordsException extends CatalogDomainException {
    
    public TooManyKeywordsException(String message) {
        super(message);
    }
}