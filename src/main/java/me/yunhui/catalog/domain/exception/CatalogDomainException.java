package me.yunhui.catalog.domain.exception;

import me.yunhui.shared.domain.DomainException;

public abstract class CatalogDomainException extends DomainException {
    
    protected CatalogDomainException(String message) {
        super(message);
    }
    
    protected CatalogDomainException(String message, String errorCode) {
        super(message, errorCode);
    }
    
    protected CatalogDomainException(String message, Throwable cause) {
        super(message, cause);
    }
    
    protected CatalogDomainException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
    
    @Override
    public final String getDomainName() {
        return "CATALOG";
    }
}