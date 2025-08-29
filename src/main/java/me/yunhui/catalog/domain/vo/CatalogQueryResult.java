package me.yunhui.catalog.domain.vo;

import me.yunhui.catalog.domain.entity.CatalogItem;
import java.util.List;

public record CatalogQueryResult(
    List<CatalogItem> items,
    long totalCount
) {

    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    public int size() {
        return items.size();
    }
}
