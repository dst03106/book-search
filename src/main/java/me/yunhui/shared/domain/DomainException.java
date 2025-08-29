package me.yunhui.shared.domain;

import java.time.LocalDateTime;

public abstract class DomainException extends RuntimeException {
    
    private final String errorCode;
    private final LocalDateTime occurredAt;
    
    protected DomainException(String message) {
        this(message, null, null);
    }
    
    protected DomainException(String message, String errorCode) {
        this(message, errorCode, null);
    }
    
    protected DomainException(String message, Throwable cause) {
        this(message, null, cause);
    }
    
    protected DomainException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.occurredAt = LocalDateTime.now();
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
    
    public abstract String getDomainName();
}