package me.yunhui.domain;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
    
    Optional<Book> findById(BookId id);
    
    List<Book> findByKeyword(String keyword);
    
    Book save(Book book);
}