package me.yunhui.infrastructure;

import me.yunhui.domain.Book;
import me.yunhui.domain.BookId;
import me.yunhui.domain.BookRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class BookRepositoryImpl implements BookRepository {

    private final BookDocumentRepository documentRepository;
    private final BookMapper mapper;

    public BookRepositoryImpl(BookDocumentRepository documentRepository, BookMapper mapper) {
        this.documentRepository = documentRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Book> findById(BookId id) {
        return documentRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public List<Book> findByKeyword(String keyword) {
        return documentRepository.findByTitleContainingOrAuthorContainingOrDescriptionContaining(
                keyword, keyword, keyword)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Book save(Book book) {
        BookDocument document = mapper.toDocument(book);
        BookDocument savedDocument = documentRepository.save(document);
        return mapper.toDomain(savedDocument);
    }
}