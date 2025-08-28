package me.yunhui.catalog.domain.exception;

public abstract class CatalogDomainException extends RuntimeException {
    
    protected CatalogDomainException(String message) {
        super(message);
    }
    
    protected CatalogDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}