package me.yunhui.infrastructure;

import me.yunhui.domain.Book;
import me.yunhui.domain.BookId;
import me.yunhui.domain.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class BookRepositoryImplIntegrationTest {

    @Container
    static GenericContainer<?> elasticsearchContainer = new GenericContainer<>(
            DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:8.12.0"))
            .withExposedPorts(9200)
            .withEnv("discovery.type", "single-node")
            .withEnv("xpack.security.enabled", "false")
            .withEnv("xpack.security.enrollment.enabled", "false")
            .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("elasticsearch.host", elasticsearchContainer::getHost);
        registry.add("elasticsearch.port", elasticsearchContainer::getFirstMappedPort);
        registry.add("spring.data.elasticsearch.uris", 
                () -> "http://" + elasticsearchContainer.getHost() + ":" + elasticsearchContainer.getFirstMappedPort());
    }

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookDocumentRepository documentRepository;

    @BeforeEach
    void setUp() {
        documentRepository.deleteAll();
    }

    @Test
    void shouldSaveBookAndRetrieveById() {
        // Given
        BookId bookId = BookId.generate();
        Book book = new Book(
                bookId,
                "Clean Code",
                "A Handbook of Agile Software Craftsmanship",
                "Robert C. Martin",
                "9780132350884",
                "Prentice Hall",
                LocalDate.of(2008, 8, 1),
                "https://example.com/clean-code.jpg",
                "Clean Code is a book about writing maintainable software."
        );

        // When
        Book savedBook = bookRepository.save(book);

        // Then
        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getId()).isEqualTo(bookId);
        assertThat(savedBook.getTitle()).isEqualTo("Clean Code");
        assertThat(savedBook.getAuthor()).isEqualTo("Robert C. Martin");
        assertThat(savedBook.getIsbn()).isEqualTo("9780132350884");

        // Verify it can be retrieved
        Optional<Book> retrievedBook = bookRepository.findById(bookId);
        assertThat(retrievedBook).isPresent();
        assertThat(retrievedBook.get().getTitle()).isEqualTo("Clean Code");
    }

    @Test
    void shouldSaveBookWithMinimalFields() {
        // Given
        BookId bookId = BookId.generate();
        Book book = new Book(bookId, "Test Driven Development", "Kent Beck", "9780321146533");

        // When
        Book savedBook = bookRepository.save(book);

        // Then
        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getId()).isEqualTo(bookId);
        assertThat(savedBook.getTitle()).isEqualTo("Test Driven Development");
        assertThat(savedBook.getAuthor()).isEqualTo("Kent Beck");
        assertThat(savedBook.getIsbn()).isEqualTo("9780321146533");
        assertThat(savedBook.getSubtitle()).isNull();
        assertThat(savedBook.getPublisher()).isNull();
    }

    @Test
    void shouldFindBookByKeyword() {
        // Given
        BookId bookId = BookId.generate();
        Book book = new Book(
                bookId,
                "Spring Boot in Action",
                "Craig Walls",
                "9781617292545"
        );
        bookRepository.save(book);

        // When
        var books = bookRepository.findByKeyword("Spring");

        // Then
        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("Spring Boot in Action");
    }

    @Test
    void shouldFindBookByKeywordInAuthor() {
        // Given
        BookId bookId = BookId.generate();
        Book book = new Book(
                bookId,
                "Effective Java",
                "Joshua Bloch",
                "9780134685991"
        );
        bookRepository.save(book);

        // When
        var books = bookRepository.findByKeyword("Joshua");

        // Then
        assertThat(books).hasSize(1);
        assertThat(books.get(0).getAuthor()).isEqualTo("Joshua Bloch");
    }
}