package me.yunhui.catalog;

import me.yunhui.catalog.application.DataImportService;
import me.yunhui.catalog.application.PopularKeywordService;
import me.yunhui.catalog.domain.entity.CatalogItem;
import me.yunhui.catalog.domain.vo.CatalogKeyword;
import me.yunhui.catalog.interfaces.dto.CatalogSearchResponse;
import me.yunhui.catalog.interfaces.dto.PopularKeywordResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
class PopularKeywordIntegrationTest {

    @Container
    static ElasticsearchContainer elasticsearch = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.8.0")
            .withEnv("discovery.type", "single-node")
            .withEnv("ES_JAVA_OPTS", "-Xms256m -Xmx256m")
            .withEnv("xpack.security.enabled", "false");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7.0-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("elasticsearch.host", elasticsearch::getHost);
        registry.add("elasticsearch.port", elasticsearch::getFirstMappedPort);
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", redis::getFirstMappedPort);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PopularKeywordService popularKeywordService;

    @Autowired
    private DataImportService dataImportService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private String baseUrl;
    private String searchUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/catalog/popular-keywords";
        searchUrl = "http://localhost:" + port + "/api/catalog";
        // Redis 클리어
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    @Order(1)
    void setUp_카탈로그_데이터_임포트() {
        // Given: 테스트용 책 데이터
        List<CatalogItem> testBooks = List.of(
            CatalogItem.create(
                "1",
                "Java Programming Guide", 
                "Complete Guide to Java Development",
                "John Doe",
                "978-0123456789",
                "Tech Books",
                LocalDate.of(2023, 1, 1),
                "https://example.com/java-book.jpg"
            ),
            CatalogItem.create(
                "2",
                "Spring Framework Essentials",
                "Master Spring Boot and Spring Framework", 
                "Jane Smith",
                "978-0234567890",
                "Developer Press",
                LocalDate.of(2023, 2, 1),
                "https://example.com/spring-book.jpg"
            ),
            CatalogItem.create(
                "3",
                "React Development Handbook",
                "Modern React Development Techniques",
                "Bob Wilson",
                "978-0345678901", 
                "Frontend Publishing",
                LocalDate.of(2023, 3, 1),
                "https://example.com/react-book.jpg"
            ),
            CatalogItem.create(
                "4", 
                "JavaScript: The Complete Reference",
                "Everything you need to know about JavaScript",
                "Alice Brown",
                "978-0456789012",
                "Web Dev Books",
                LocalDate.of(2023, 4, 1), 
                "https://example.com/js-book.jpg"
            ),
            CatalogItem.create(
                "5", 
                "Python Data Science",
                "Data analysis and machine learning with Python",
                "Charlie Davis",
                "978-0567890123",
                "Data Science Press",
                LocalDate.of(2023, 5, 1), 
                "https://example.com/python-book.jpg"
            )
        );

        // When: 데이터 임포트
        dataImportService.importCatalogItems(testBooks);
        
        // Then: 성공적으로 완료되어야 함
        assertDoesNotThrow(() -> dataImportService.importCatalogItems(testBooks));
        
        // Elasticsearch 인덱싱 대기 시간
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    @Order(2)
    void 검색_수행으로_인기_검색어_생성() {
        // Given: 여러 검색어로 검색을 수행하여 인기 검색어 생성
        
        // Java 검색 (5번)
        for (int i = 0; i < 5; i++) {
            restTemplate.getForEntity(searchUrl + "?q=java&page=0&size=10", CatalogSearchResponse.class);
        }
        
        // Spring 검색 (4번)
        for (int i = 0; i < 4; i++) {
            restTemplate.getForEntity(searchUrl + "?q=spring&page=0&size=10", CatalogSearchResponse.class);
        }
        
        // React 검색 (3번)
        for (int i = 0; i < 3; i++) {
            restTemplate.getForEntity(searchUrl + "?q=react&page=0&size=10", CatalogSearchResponse.class);
        }
        
        // JavaScript 검색 (2번)
        for (int i = 0; i < 2; i++) {
            restTemplate.getForEntity(searchUrl + "?q=javascript&page=0&size=10", CatalogSearchResponse.class);
        }
        
        // Python 검색 (1번)
        restTemplate.getForEntity(searchUrl + "?q=python&page=0&size=10", CatalogSearchResponse.class);
        
        // When: 상위 5개 인기 검색어 조회
        List<CatalogKeyword> result = popularKeywordService.getTopPopularKeywords(5);
        
        // Then: 검색 횟수 순으로 정렬되어야 함
        assertNotNull(result);
        assertEquals(5, result.size());
        assertEquals("java", result.get(0).value());
        assertEquals("spring", result.get(1).value());
        assertEquals("react", result.get(2).value());
        assertEquals("javascript", result.get(3).value());
        assertEquals("python", result.get(4).value());
    }

    @Test
    @Order(3)
    void 인기_검색어_상위_10개_조회() {
        // When: PopularKeywordService를 통한 상위 10개 조회 (기존 검색 데이터 유지)
        List<CatalogKeyword> result = popularKeywordService.getTop10PopularKeywords();
        
        // Then: 검증 - 앞서 검색한 5개 키워드가 포함되어야 함
        assertNotNull(result);
        assertTrue(result.size() <= 10); // 최대 10개이지만 실제로는 5개만 있을 수 있음
        assertTrue(result.size() >= 5);  // 최소 5개는 있어야 함
        assertEquals("java", result.get(0).value());
        assertEquals("spring", result.get(1).value());
        assertEquals("react", result.get(2).value());
        assertEquals("javascript", result.get(3).value());
        assertEquals("python", result.get(4).value());
    }

    @Test
    @Order(4)
    void 인기_검색어_REST_API_테스트() {
        // When: REST API 호출
        ResponseEntity<PopularKeywordResponse> response = restTemplate.getForEntity(
            baseUrl, PopularKeywordResponse.class
        );
        
        // Then: 응답 검증
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        PopularKeywordResponse body = response.getBody();
        assertNotNull(body);
        assertNotNull(body.keywords());
        assertTrue(body.keywords().size() <= 10);
        assertTrue(body.keywords().size() >= 5);
        assertEquals("java", body.keywords().get(0).keyword());
        assertEquals("spring", body.keywords().get(1).keyword());
    }

    @Test
    @Order(5)
    void 추가_검색으로_순위_변경_테스트() {
        // Given: Python을 많이 검색하여 순위 변경
        for (int i = 0; i < 10; i++) {
            restTemplate.getForEntity(searchUrl + "?q=python&page=0&size=10", CatalogSearchResponse.class);
        }
        
        // When: 인기 검색어 조회
        List<CatalogKeyword> result = popularKeywordService.getTop10PopularKeywords();
        
        // Then: Python이 1위가 되어야 함
        assertNotNull(result);
        assertTrue(result.size() >= 5);
        assertEquals("python", result.get(0).value()); // Python이 1위로 올라옴
        assertEquals("java", result.get(1).value());   // Java가 2위로 내려감
    }

    @Test
    @Order(6)
    void 복합_검색어_인기도_테스트() {
        // Given: 복합 검색어들로 검색 수행
        restTemplate.getForEntity(searchUrl + "?q=spring boot&page=0&size=10", CatalogSearchResponse.class);
        restTemplate.getForEntity(searchUrl + "?q=spring boot&page=0&size=10", CatalogSearchResponse.class);
        restTemplate.getForEntity(searchUrl + "?q=react hooks&page=0&size=10", CatalogSearchResponse.class);
        
        // When: 인기 검색어 조회
        List<CatalogKeyword> result = popularKeywordService.getTop10PopularKeywords();
        
        // Then: 복합 검색어도 포함되어야 함
        assertNotNull(result);
        assertTrue(result.size() >= 5);
        
        List<String> keywords = result.stream().map(CatalogKeyword::value).toList();
        assertTrue(keywords.contains("spring") || keywords.contains("boot") || keywords.contains("spring boot"));
    }

    @Test
    @Order(7)
    void 인기_검색어_초기화_후_새로운_검색_테스트() {
        // Given: Redis 데이터 클리어
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        
        // 새로운 검색어로 검색 수행
        restTemplate.getForEntity(searchUrl + "?q=algorithm&page=0&size=10", CatalogSearchResponse.class);
        restTemplate.getForEntity(searchUrl + "?q=algorithm&page=0&size=10", CatalogSearchResponse.class);
        restTemplate.getForEntity(searchUrl + "?q=data structure&page=0&size=10", CatalogSearchResponse.class);
        
        // When: REST API 호출
        ResponseEntity<PopularKeywordResponse> response = restTemplate.getForEntity(
            baseUrl, PopularKeywordResponse.class
        );
        
        // Then: 새로운 검색어들이 반환되어야 함
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        PopularKeywordResponse body = response.getBody();
        assertNotNull(body);
        assertNotNull(body.keywords());
        assertTrue(body.keywords().size() > 0);
        
        List<String> keywordValues = body.keywords().stream()
                .map(PopularKeywordResponse.PopularKeywordDto::keyword)
                .toList();
        assertTrue(keywordValues.contains("algorithm") || 
                   keywordValues.contains("data") || 
                   keywordValues.contains("structure") ||
                   keywordValues.contains("data structure"));
    }
}
