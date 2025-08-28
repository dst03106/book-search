package me.yunhui.catalog.interfaces.rest;

import me.yunhui.catalog.application.CatalogService;
import me.yunhui.catalog.domain.CatalogQuery;
import me.yunhui.catalog.domain.CatalogResult;
import me.yunhui.catalog.interfaces.dto.CatalogSearchRequest;
import me.yunhui.catalog.interfaces.dto.CatalogSearchResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {
    
    private final CatalogService catalogService;
    
    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
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
        CatalogResult result = catalogService.search(query);
        
        long executionTime = System.currentTimeMillis() - startTime;
        
        // Response DTO로 변환
        List<CatalogSearchResponse.CatalogItemDto> items = result.getItems()
                .stream()
                .map(resultItem -> new CatalogSearchResponse.CatalogItemDto(
                    resultItem.id(),
                    resultItem.title(),
                    resultItem.subtitle(),
                    resultItem.imageUrl(),  // image 파라미터에 imageUrl 값 전달
                    resultItem.author(),
                    resultItem.isbn(),
                    resultItem.published()
                ))
                .toList();
        
        CatalogSearchResponse response = CatalogSearchResponse.of(
            request.q(),
            items, 
            result.getTotalElements(), 
            result.getPage(), 
            result.getSize(),
            executionTime,
            "MULTI_MATCH" // 현재는 MULTI MATCH만 지원
        );
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/popular")
    public ResponseEntity<List<String>> getPopularSearchTerms() {
        // TODO: 인기 검색어 TOP10 구현 (나중에)
        return ResponseEntity.ok(List.of());
    }
}
