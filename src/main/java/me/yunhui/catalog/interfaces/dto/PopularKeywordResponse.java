package me.yunhui.catalog.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "인기 검색어 응답")
public record PopularKeywordResponse(
    @Schema(description = "인기 검색어 목록")
    List<PopularKeywordDto> keywords,
    @Schema(description = "전체 개수", example = "10")
    int totalCount
) {
    
    public static PopularKeywordResponse of(List<String> keywords) {
        List<PopularKeywordDto> keywordDtos = keywords.stream()
                .map(PopularKeywordDto::new)
                .toList();
        
        return new PopularKeywordResponse(keywordDtos, keywordDtos.size());
    }
    
    @Schema(description = "인기 검색어 정보")
    public record PopularKeywordDto(
        @Schema(description = "검색어", example = "java")
        String keyword
    ) {}
}
