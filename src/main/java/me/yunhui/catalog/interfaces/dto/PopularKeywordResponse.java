package me.yunhui.catalog.interfaces.dto;

import java.util.List;

public record PopularKeywordResponse(
    List<PopularKeywordDto> keywords,
    int totalCount
) {
    
    public static PopularKeywordResponse of(List<String> keywords) {
        List<PopularKeywordDto> keywordDtos = keywords.stream()
                .map(PopularKeywordDto::new)
                .toList();
        
        return new PopularKeywordResponse(keywordDtos, keywordDtos.size());
    }
    
    public record PopularKeywordDto(String keyword) {}
}