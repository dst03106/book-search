package me.yunhui.catalog.domain.vo;

import me.yunhui.catalog.domain.entity.CatalogItem;
import java.util.List;

public record CatalogQueryResult(
    List<CatalogItem> items,
    long totalCount
) {
    
    public CatalogQueryResult {
        if (items == null) {
            throw new IllegalArgumentException("Items cannot be null");
        }
        if (totalCount < 0) {
            throw new IllegalArgumentException("Total count cannot be negative");
        }
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    public int size() {
        return items.size();
    }
}
