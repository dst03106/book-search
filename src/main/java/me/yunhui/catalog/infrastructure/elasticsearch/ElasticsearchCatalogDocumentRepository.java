package me.yunhui.catalog.infrastructure.elasticsearch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;

public interface ElasticsearchCatalogDocumentRepository extends ElasticsearchRepository<CatalogDocument, String>, ElasticsearchCatalogDocumentRepositoryCustom {

    @Query("""
        {
            "multi_match": {
                "query": "?0",
                "fields": ["title^3", "subtitle^2", "author^2"],
                "type": "best_fields",
                "fuzziness": "AUTO",
                "minimum_should_match": "75%"
            }
        }
        """)
    Page<CatalogDocument> smartSearch(String query, Pageable pageable);

}