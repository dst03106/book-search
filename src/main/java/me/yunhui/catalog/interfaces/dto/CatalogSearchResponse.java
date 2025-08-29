package me.yunhui.catalog.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "도서 검색 응답")
public record CatalogSearchResponse(
    @Schema(description = "검색 쿼리", example = "java spring")
    String searchQuery,
    @Schema(description = "페이지 정보")
    PageInfo pageInfo,
    @Schema(description = "검색 결과 목록")
    List<CatalogItemDto> data,
    @Schema(description = "검색 메타데이터")
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
    
    @Schema(description = "페이지 정보")
    public record PageInfo(
        @Schema(description = "현재 페이지 (1부터 시작)", example = "1")
        int currentPage,
        @Schema(description = "페이지 크기", example = "10")
        int pageSize,
        @Schema(description = "전체 페이지 수", example = "5")
        int totalPages,
        @Schema(description = "전체 요소 수", example = "50")
        long totalElements
    ) {}
    
    @Schema(description = "검색 메타데이터")
    public record SearchMetadata(
        @Schema(description = "실행 시간 (밀리초)", example = "123")
        long executionTime,
        @Schema(description = "검색 전략", example = "MULTI_MATCH")
        String strategy
    ) {}
    
    @Schema(description = "도서 정보")
    public record CatalogItemDto(
        @Schema(description = "도서 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,
        @Schema(description = "도서 제목", example = "Effective Java")
        String title,
        @Schema(description = "부제목", example = "Best Practices Guide")
        String subtitle,
        @Schema(description = "이미지 URL", example = "https://example.com/book.png")
        String image,
        @Schema(description = "저자", example = "Joshua Bloch")
        String author,
        @Schema(description = "ISBN", example = "9780134685991")
        String isbn,
        @Schema(description = "출판일", example = "2018-01-06")
        LocalDate published
    ) {}
}

