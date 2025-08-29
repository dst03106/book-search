package me.yunhui.catalog.interfaces.rest;

import me.yunhui.catalog.application.CatalogService;
import me.yunhui.catalog.application.PopularKeywordService;
import me.yunhui.catalog.domain.vo.CatalogKeyword;
import me.yunhui.catalog.domain.vo.CatalogQuery;
import me.yunhui.catalog.domain.vo.CatalogQueryResult;
import me.yunhui.catalog.interfaces.dto.CatalogSearchRequest;
import me.yunhui.catalog.interfaces.dto.CatalogSearchResponse;
import me.yunhui.catalog.interfaces.dto.PopularKeywordResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {
    
    private final CatalogService catalogService;
    private final PopularKeywordService popularKeywordService;
    
    public CatalogController(CatalogService catalogService, PopularKeywordService popularKeywordService) {
        this.catalogService = catalogService;
        this.popularKeywordService = popularKeywordService;
    }
    
    @GetMapping
    public ResponseEntity<CatalogSearchResponse> searchCatalog(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        long startTime = System.currentTimeMillis();
        
        // Request 검증
        CatalogSearchRequest request = new CatalogSearchRequest(q, page, size);
        
        // Domain 객체로 변환
        CatalogQuery query = CatalogQuery.of(
            request.q(), 
            request.getPageOrDefault(), 
            request.getSizeOrDefault()
        );
        
        // Service 호출
        CatalogQueryResult result = catalogService.search(query);
        
        long executionTime = System.currentTimeMillis() - startTime;
        
        // Response DTO로 변환
        List<CatalogSearchResponse.CatalogItemDto> items = result.items()
                .stream()
                .map(catalogItem -> new CatalogSearchResponse.CatalogItemDto(
                    catalogItem.id(),
                    catalogItem.title(),
                    catalogItem.subtitle(),
                    catalogItem.imageUrl(),
                    catalogItem.author(),
                    catalogItem.isbn(),
                    catalogItem.published()
                ))
                .toList();
        
        CatalogSearchResponse response = CatalogSearchResponse.of(
            request.q(),
            items, 
            result.totalCount(), 
            query.getPage(), 
            query.getSize(),
            executionTime,
            "MULTI_MATCH" // 현재는 MULTI MATCH만 지원
        );
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/searches/popular-terms")
    public ResponseEntity<PopularKeywordResponse> getPopularSearchTerms() {
        List<CatalogKeyword> popularKeywords = popularKeywordService.getTop10PopularKeywords();
        List<String> keywordValues = popularKeywords.stream()
                .map(CatalogKeyword::value)
                .toList();
        
        PopularKeywordResponse response = PopularKeywordResponse.of(keywordValues);
        return ResponseEntity.ok(response);
    }
}
