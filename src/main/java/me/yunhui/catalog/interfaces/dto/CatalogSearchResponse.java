package me.yunhui.catalog.interfaces.dto;

import java.time.LocalDate;
import java.util.List;

public record CatalogSearchResponse(
    String searchQuery,
    PageInfo pageInfo,
    List<CatalogItemDto> data,
    SearchMetadata searchMetadata
) {
    public static CatalogSearchResponse of(
        String searchQuery,
        List<CatalogItemDto> data, 
        long totalElements, 
        int page, 
        int size,
        long executionTime,
        String strategy
    ) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        
        PageInfo pageInfo = new PageInfo(
            page + 1, // API에서는 1부터 시작
            size,
            totalPages,
            totalElements
        );
        
        SearchMetadata metadata = new SearchMetadata(executionTime, strategy);
        
        return new CatalogSearchResponse(searchQuery, pageInfo, data, metadata);
    }
    
    public record PageInfo(
        int currentPage,
        int pageSize,
        int totalPages,
        long totalElements
    ) {}
    
    public record SearchMetadata(
        long executionTime,
        String strategy
    ) {}
    
    public record CatalogItemDto(
        String id,
        String title,
        String subtitle,
        String image,
        String author,
        String isbn,
        LocalDate published
    ) {}
}
