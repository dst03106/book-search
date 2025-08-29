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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
@Tag(name = "카탈로그 API", description = "도서 검색 및 인기 검색어 조회 API")
public class CatalogController {
    
    private final CatalogService catalogService;
    private final PopularKeywordService popularKeywordService;
    
    public CatalogController(CatalogService catalogService, PopularKeywordService popularKeywordService) {
        this.catalogService = catalogService;
        this.popularKeywordService = popularKeywordService;
    }
    
    @GetMapping
    @Operation(summary = "도서 검색", description = "키워드를 통해 도서를 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공",
                    content = @Content(schema = @Schema(implementation = CatalogSearchResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터")
    })
    public ResponseEntity<CatalogSearchResponse> searchCatalog(
            @Parameter(description = "검색 키워드", example = "java spring") 
            @RequestParam String q,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기 (1-100)", example = "10") 
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
    
    @GetMapping("/popular-keywords")
    @Operation(summary = "인기 검색어 조회", description = "상위 10개의 인기 검색어를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PopularKeywordResponse.class)))
    })
    public ResponseEntity<PopularKeywordResponse> getPopularKeywords() {
        List<CatalogKeyword> popularKeywords = popularKeywordService.getTop10PopularKeywords();
        List<String> keywordValues = popularKeywords.stream()
                .map(CatalogKeyword::value)
                .toList();
        
        PopularKeywordResponse response = PopularKeywordResponse.of(keywordValues);
        return ResponseEntity.ok(response);
    }
}

