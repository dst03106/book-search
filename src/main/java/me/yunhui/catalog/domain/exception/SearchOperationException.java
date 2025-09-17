package me.yunhui.catalog.domain.exception;

public class SearchOperationException extends CatalogDomainException {

    public SearchOperationException(String message) {
        super(message);
    }

    public SearchOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getErrorCode() {
        return "SEARCH_OPERATION_FAILED";
    }
}