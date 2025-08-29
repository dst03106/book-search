package me.yunhui.catalog.application;

import me.yunhui.catalog.domain.entity.CatalogItem;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class DataSeedService {
    
    private final DataImportService dataImportService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final Executor executor;
    
    private static final int MAX_CONCURRENT_REQUESTS = 5;
    private static final int RATE_LIMIT_DELAY_MS = 200;
    
    private static final String IT_BOOKSTORE_API = "https://api.itbook.store/1.0";
    private static final List<String> SEARCH_KEYWORDS = List.of(
        "java", "python", "javascript", "spring", "react", "docker", "kubernetes",
        "mysql", "mongodb", "redis", "linux", "aws", "azure", "gcp",
        "machine learning", "data science", "ai", "blockchain", "microservices",
        "devops", "angular", "vue", "nodejs", "go", "rust", "swift", "kotlin",
        "android", "ios", "web", "api", "git", "jenkins", "terraform"
    );
    
    public DataSeedService(DataImportService dataImportService) {
        this.dataImportService = dataImportService;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.executor = Executors.newFixedThreadPool(MAX_CONCURRENT_REQUESTS);
    }
    
    public void seedBooksFromItBookstore() {
        Set<String> isbnSet = ConcurrentHashMap.newKeySet();
        List<CatalogItem> allBooks = new ArrayList<>();
        
        // 비동기로 모든 키워드에 대해 API 호출
        List<CompletableFuture<List<CatalogItem>>> futures = SEARCH_KEYWORDS.stream()
            .map(keyword -> fetchBooksFromApiAsync(keyword, isbnSet))
            .collect(Collectors.toList());
        
        // 모든 비동기 작업 완료 대기
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );
        
        try {
            // 최대 60초 대기
            allFutures.get(60, TimeUnit.SECONDS);
            
            // 결과 수집
            for (CompletableFuture<List<CatalogItem>> future : futures) {
                if (future.isDone() && !future.isCompletedExceptionally()) {
                    List<CatalogItem> books = future.get();
                    synchronized (allBooks) {
                        allBooks.addAll(books);
                        if (allBooks.size() >= 100) {
                            break;
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            System.out.println("Error while waiting for API calls to complete: " + e.getMessage());
        }
        
        if (!allBooks.isEmpty()) {
            dataImportService.importCatalogItems(allBooks);
            System.out.println("Successfully imported " + allBooks.size() + " books from IT Bookstore API");
        } else {
            System.out.println("No books were imported");
        }
    }
    
    private CompletableFuture<List<CatalogItem>> fetchBooksFromApiAsync(String keyword, Set<String> isbnSet) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Rate limiting을 위한 지연
                Thread.sleep(RATE_LIMIT_DELAY_MS);
                return fetchBooksFromApi(keyword, isbnSet);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Interrupted while processing keyword: " + keyword);
                return new ArrayList<>();
            } catch (Exception e) {
                System.out.println("Failed to fetch books for keyword: " + keyword + ", error: " + e.getMessage());
                return new ArrayList<>();
            }
        }, executor);
    }
    
    private List<CatalogItem> fetchBooksFromApi(String keyword, Set<String> isbnSet) {
        List<CatalogItem> books = new ArrayList<>();
        
        try {
            String url = IT_BOOKSTORE_API + "/search/" + keyword.replace(" ", "%20");
            String response = restTemplate.getForObject(url, String.class);
            
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode booksNode = rootNode.get("books");
            
            if (booksNode != null && booksNode.isArray()) {
                for (JsonNode bookNode : booksNode) {
                    try {
                        String isbn = bookNode.get("isbn13").asText();
                        if (isbnSet.contains(isbn)) {
                            continue;
                        }
                        isbnSet.add(isbn);
                        
                        CatalogItem catalogItem = createCatalogItemFromJson(bookNode);
                        books.add(catalogItem);
                        
                    } catch (Exception e) {
                        System.out.println("Failed to parse book: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("API call failed for keyword: " + keyword + ", error: " + e.getMessage());
        }
        
        return books;
    }
    
    private CatalogItem createCatalogItemFromJson(JsonNode bookNode) {
        String id = UUID.randomUUID().toString();
        String title = bookNode.get("title").asText("");
        String subtitle = bookNode.get("subtitle").asText("");
        String author = extractAuthorFromTitle(title);
        String isbn = bookNode.get("isbn13").asText("");
        String publisher = extractPublisherFromTitle(title);
        LocalDate published = parsePublicationDate(title);
        String imageUrl = bookNode.get("image").asText("");
        
        return CatalogItem.create(id, title, subtitle, author, isbn, publisher, published, imageUrl);
    }
    
    private String extractAuthorFromTitle(String title) {
        if (title.contains(" by ")) {
            String[] parts = title.split(" by ");
            if (parts.length > 1) {
                return parts[1].split(",")[0].trim();
            }
        }
        return "Unknown Author";
    }
    
    private String extractPublisherFromTitle(String title) {
        String[] publishers = {"O'Reilly", "Manning", "Apress", "Packt", "Wrox", "Addison-Wesley", 
                              "McGraw-Hill", "Pearson", "No Starch Press", "Pragmatic Bookshelf"};
        
        String titleLower = title.toLowerCase();
        for (String publisher : publishers) {
            if (titleLower.contains(publisher.toLowerCase())) {
                return publisher;
            }
        }
        return "IT Publisher";
    }
    
    private LocalDate parsePublicationDate(String title) {
        try {
            if (title.contains("2024")) return LocalDate.of(2024, 1, 1);
            if (title.contains("2023")) return LocalDate.of(2023, 6, 15);
            if (title.contains("2022")) return LocalDate.of(2022, 3, 10);
            if (title.contains("2021")) return LocalDate.of(2021, 8, 20);
            if (title.contains("2020")) return LocalDate.of(2020, 5, 12);
        } catch (Exception e) {
            // 파싱 실패 시 기본값 반환
        }
        return LocalDate.of(2023, 1, 1);
    }
}