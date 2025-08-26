package me.yunhui.infrastructure;

import me.yunhui.domain.Book;
import me.yunhui.domain.BookId;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public Book toDomain(BookDocument document) {
        return new Book(
                BookId.of(document.getId()),
                document.getTitle(),
                document.getSubtitle(),
                document.getAuthor(),
                document.getIsbn(),
                document.getPublisher(),
                document.getPublished(),
                document.getImageUrl(),
                document.getDescription()
        );
    }

    public BookDocument toDocument(Book book) {
        return new BookDocument(
                book.getId().getValue(),
                book.getTitle(),
                book.getSubtitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getPublisher(),
                book.getPublished(),
                book.getImageUrl(),
                book.getDescription(),
                book.getCreatedAt(),
                book.getUpdatedAt()
        );
    }
}