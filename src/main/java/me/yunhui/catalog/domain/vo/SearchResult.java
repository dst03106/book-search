package me.yunhui.catalog.domain.vo;

import me.yunhui.catalog.domain.entity.CatalogItem;
import java.util.List;

public record SearchResult(
    List<CatalogItem> items,
    long totalCount
) {
    
    public SearchResult {
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
