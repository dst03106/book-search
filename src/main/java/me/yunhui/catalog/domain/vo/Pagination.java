package me.yunhui.catalog.domain.vo;

import me.yunhui.catalog.domain.exception.InvalidPageNumberException;
import me.yunhui.catalog.domain.exception.InvalidPageSizeException;

public record Pagination(
    int page,
    int size
) {
    
    public Pagination {
        if (page < 0) {
            throw new InvalidPageNumberException("페이지 번호는 0 이상이어야 합니다");
        }
        if (size <= 0) {
            throw new InvalidPageSizeException("페이지 크기는 1 이상이어야 합니다");
        }
    }
    
    public int offset() {
        return page * size;
    }
}
