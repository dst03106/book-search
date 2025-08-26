package me.yunhui.infrastructure;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookDocumentRepository extends ElasticsearchRepository<BookDocument, String> {
    
    List<BookDocument> findByTitleContainingOrAuthorContainingOrDescriptionContaining(
            String title, String author, String description);
}