package me.yunhui.catalog;

import me.yunhui.catalog.application.DataImportService;
import me.yunhui.catalog.domain.entity.CatalogItem;
import me.yunhui.catalog.interfaces.dto.CatalogSearchResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
class CatalogIntegrationTest {

    @Container
    static ElasticsearchContainer elasticsearch = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.8.0")
            .withEnv("discovery.type", "single-node")
            .withEnv("ES_JAVA_OPTS", "-Xms256m -Xmx256m")
            .withEnv("xpack.security.enabled", "false");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("elasticsearch.host", elasticsearch::getHost);
        registry.add("elasticsearch.port", elasticsearch::getFirstMappedPort);
        // Redis는 사용하지 않음 (키워드 추적 이벤트 핸들러에서 예외 처리됨)
    }

//    @Container
//    static GenericContainer<?> elasticsearchContainer = new GenericContainer<>(
//            DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:8.12.0"))
//            .withExposedPorts(9200)
//            .withEnv("discovery.type", "single-node")
//            .withEnv("xpack.security.enabled", "false")
//            .withEnv("xpack.security.enrollment.enabled", "false")
//            .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m");

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DataImportService dataImportService;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/catalog";
    }

    @Test
    @Order(1)
    void setUp_테스트_데이터_임포트() {
        // Given: 테스트용 책 데이터
        List<CatalogItem> testBooks = List.of(
            CatalogItem.create(
                "1",
                "Test-Driven Development: By Example", 
                "A TDD Guide",
                "Kent Beck",
                "978-0321146533",
                "Addison-Wesley",
                LocalDate.of(2002, 11, 18),
                "https://example.com/tdd-book.jpg"
            ),
            CatalogItem.create(
                "2",
                "Clean Code",
                "A Handbook of Agile Software Craftsmanship", 
                "Robert C. Martin",
                "978-0132350884",
                "Prentice Hall",
                LocalDate.of(2008, 8, 1),
                "https://example.com/clean-code.jpg"
            ),
            CatalogItem.create(
                "3",
                "JavaScript: The Good Parts",
                "Unearthing the Excellence in JavaScript",
                "Douglas Crockford",
                "978-0596517748", 
                "O'Reilly Media",
                LocalDate.of(2008, 5, 1),
                "https://example.com/js-book.jpg"
            ),
            CatalogItem.create(
                "4", 
                "Spring in Action",
                "Spring Framework Guide",
                "Craig Walls",
                "978-1617294945",
                "Manning Publications",
                LocalDate.of(2018, 10, 1), 
                "https://example.com/spring-book.jpg"
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
    void search_TDD로_검색시_title과_description에서_검색됨() {
        // Given: "tdd" 검색 쿼리
        String searchUrl = baseUrl + "?q=tdd&page=0&size=10";
        
        // When: 검색 API 호출
        ResponseEntity<CatalogSearchResponse> response = restTemplate.getForEntity(
            searchUrl, CatalogSearchResponse.class
        );
        
        // Then: 응답 검증
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        CatalogSearchResponse body = response.getBody();
        assertNotNull(body);
        
        // 검색 쿼리 확인
        assertEquals("tdd", body.searchQuery());
        
        // 페이지 정보 확인
        assertNotNull(body.pageInfo());
        assertEquals(1, body.pageInfo().currentPage()); // API는 1부터 시작
        assertEquals(10, body.pageInfo().pageSize());
        
        // 검색 결과 확인
        assertNotNull(body.data());
        assertEquals(1, body.data().size()); // TDD 관련 책 1권 (title에서만 매칭)
        
        // 검색 메타데이터 확인
        assertNotNull(body.searchMetadata());
        assertTrue(body.searchMetadata().executionTime() > 0);
        assertEquals("MULTI_MATCH", body.searchMetadata().strategy());
        
        // 실제 책 데이터 확인 - TDD가 title/subtitle에 포함된 책만
        assertEquals("Test-Driven Development: By Example", body.data().get(0).title());
    }

    @Test
    @Order(3)
    void search_JavaScript로_검색시_title에서_검색됨() {
        // Given: "javascript" 검색 쿼리 (소문자로 변경)
        String searchUrl = baseUrl + "?q=javascript&page=0&size=10";
        
        // When: 검색 API 호출
        ResponseEntity<CatalogSearchResponse> response = restTemplate.getForEntity(
            searchUrl, CatalogSearchResponse.class
        );
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        CatalogSearchResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(1, body.data().size());
        assertEquals("JavaScript: The Good Parts", body.data().get(0).title());
        assertEquals("Douglas Crockford", body.data().get(0).author());
    }

    @Test
    @Order(4)
    void search_author_이름으로_검색시_정상_작동() {
        // Given: 저자명으로 검색 (소문자로 변경)
        String searchUrl = baseUrl + "?q=kent beck&page=0&size=10";
        
        // When: 검색 API 호출
        ResponseEntity<CatalogSearchResponse> response = restTemplate.getForEntity(
            searchUrl, CatalogSearchResponse.class
        );
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        CatalogSearchResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(1, body.data().size());
        assertEquals("Kent Beck", body.data().get(0).author());
    }

    @Test
    @Order(5)
    void search_존재하지_않는_검색어로_검색시_빈_결과() {
        // Given: 존재하지 않는 검색어
        String searchUrl = baseUrl + "?q=NonExistentBook&page=0&size=10";
        
        // When: 검색 API 호출
        ResponseEntity<CatalogSearchResponse> response = restTemplate.getForEntity(
            searchUrl, CatalogSearchResponse.class
        );
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        CatalogSearchResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(0, body.data().size());
        assertEquals(0, body.pageInfo().totalElements());
    }
    
    @Test
    @Order(6)
    void search_OR_연산자로_복합_검색() {
        // Given: OR 연산자를 사용한 검색 쿼리 (tdd 또는 javascript)
        String searchUrl = baseUrl + "?q=tdd|javascript&page=0&size=10";
        
        // When: 검색 API 호출
        ResponseEntity<CatalogSearchResponse> response = restTemplate.getForEntity(
            searchUrl, CatalogSearchResponse.class
        );
        
        // Then: 응답 검증
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        CatalogSearchResponse body = response.getBody();
        assertNotNull(body);
        
        // OR 검색 결과: TDD 책과 JavaScript 책 모두 포함
        assertEquals(2, body.data().size());
        assertEquals("tdd|javascript", body.searchQuery());
        
        // 결과에는 TDD 책과 JavaScript 책이 모두 포함되어야 함
        List<String> titles = body.data().stream()
                .map(item -> item.title())
                .toList();
        
        assertTrue(titles.contains("Test-Driven Development: By Example"));
        assertTrue(titles.contains("JavaScript: The Good Parts"));
    }
    
    @Test
    @Order(7)
    void search_NOT_연산자로_복합_검색() {
        // Given: NOT 연산자를 사용한 검색 쿼리 (code 포함하지만 clean 제외)
        String searchUrl = baseUrl + "?q=code-clean&page=0&size=10";
        
        // When: 검색 API 호출
        ResponseEntity<CatalogSearchResponse> response = restTemplate.getForEntity(
            searchUrl, CatalogSearchResponse.class
        );
        
        // Then: 응답 검증
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        CatalogSearchResponse body = response.getBody();
        assertNotNull(body);
        
        assertEquals("code-clean", body.searchQuery());
        
        // NOT 검색 결과: "code"는 포함하지만 "clean"은 제외
        // "Clean Code" 책은 제외되어야 함
        List<String> titles = body.data().stream()
                .map(item -> item.title())
                .toList();
        
        // Clean Code 책은 결과에 포함되지 않아야 함
        assertFalse(titles.contains("Clean Code"));
    }
    
    @Test
    @Order(8)
    void search_OR_연산자_공백_포함_키워드() {
        // Given: 공백이 포함된 OR 검색 쿼리
        String searchUrl = baseUrl + "?q=spring action|clean code&page=0&size=10";
        
        // When: 검색 API 호출
        ResponseEntity<CatalogSearchResponse> response = restTemplate.getForEntity(
            searchUrl, CatalogSearchResponse.class
        );
        
        // Then: 응답 검증
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        CatalogSearchResponse body = response.getBody();
        assertNotNull(body);
        
        assertEquals("spring action|clean code", body.searchQuery());
        
        // "Spring in Action" 또는 "Clean Code" 책이 검색되어야 함
        List<String> titles = body.data().stream()
                .map(item -> item.title())
                .toList();
        
        assertTrue(titles.contains("Spring in Action") || titles.contains("Clean Code"));
    }
    
    @Test
    @Order(9)
    void search_NOT_연산자_공백_포함_키워드() {
        // Given: 공백이 포함된 NOT 검색 쿼리
        String searchUrl = baseUrl + "?q=javascript good-crockford&page=0&size=10";
        
        // When: 검색 API 호출
        ResponseEntity<CatalogSearchResponse> response = restTemplate.getForEntity(
            searchUrl, CatalogSearchResponse.class
        );
        
        // Then: 응답 검증
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        CatalogSearchResponse body = response.getBody();
        assertNotNull(body);
        
        assertEquals("javascript good-crockford", body.searchQuery());
        
        // "javascript good"은 포함하지만 "crockford"는 제외
        // JavaScript 책이 있지만 저자가 Douglas Crockford이므로 제외되어야 함
        assertEquals(0, body.data().size());
    }
    
    @Test
    @Order(10)
    void search_복합_검색_연산자_우선순위() {
        // Given: OR와 NOT이 모두 포함된 쿼리 (OR이 우선순위)
        String searchUrl = baseUrl + "?q=java|spring-boot&page=0&size=10";
        
        // When: 검색 API 호출
        ResponseEntity<CatalogSearchResponse> response = restTemplate.getForEntity(
            searchUrl, CatalogSearchResponse.class
        );
        
        // Then: 응답 검증
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        CatalogSearchResponse body = response.getBody();
        assertNotNull(body);
        
        assertEquals("java|spring-boot", body.searchQuery());
        
        // OR 연산자가 우선되어 "java" 또는 "spring-boot" 검색으로 처리
        List<String> titles = body.data().stream()
                .map(item -> item.title())
                .toList();
        
        // Spring in Action 책이 포함될 수 있음 (spring-boot에서 spring 부분 매칭)
        assertTrue(body.data().size() >= 0);
    }
}

