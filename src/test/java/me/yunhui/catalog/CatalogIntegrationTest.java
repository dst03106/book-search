package me.yunhui.catalog;

import me.yunhui.catalog.application.DataImportService;
import me.yunhui.catalog.domain.CatalogItem;
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
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;
import java.time.Duration;


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
}
