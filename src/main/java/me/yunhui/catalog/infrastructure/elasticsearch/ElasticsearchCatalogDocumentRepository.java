package me.yunhui.catalog.infrastructure.elasticsearch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticsearchCatalogDocumentRepository extends ElasticsearchRepository<CatalogDocument, String> {
    
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
    
    @Query("""
        {
            "bool": {
                "should": [
                    {
                        "multi_match": {
                            "query": "?0",
                            "fields": ["title^3", "subtitle^2", "author^2"],
                            "type": "best_fields",
                            "fuzziness": "AUTO"
                        }
                    },
                    {
                        "multi_match": {
                            "query": "?1",
                            "fields": ["title^3", "subtitle^2", "author^2"],
                            "type": "best_fields",
                            "fuzziness": "AUTO"
                        }
                    }
                ],
                "minimum_should_match": 1
            }
        }
        """)
    Page<CatalogDocument> orSearch(String keyword1, String keyword2, Pageable pageable);
    
    @Query("""
        {
            "bool": {
                "must": [
                    {
                        "multi_match": {
                            "query": "?0",
                            "fields": ["title^3", "subtitle^2", "author^2"],
                            "type": "best_fields",
                            "fuzziness": "AUTO"
                        }
                    }
                ],
                "must_not": [
                    {
                        "multi_match": {
                            "query": "?1",
                            "fields": ["title^3", "subtitle^2", "author^2"],
                            "type": "best_fields",
                            "fuzziness": "AUTO"
                        }
                    }
                ]
            }
        }
        """)
    Page<CatalogDocument> notSearch(String includeKeyword, String excludeKeyword, Pageable pageable);
}
