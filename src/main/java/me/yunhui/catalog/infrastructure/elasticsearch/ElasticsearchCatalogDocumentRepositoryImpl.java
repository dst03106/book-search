package me.yunhui.catalog.infrastructure.elasticsearch;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import me.yunhui.catalog.domain.exception.SearchOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ElasticsearchCatalogDocumentRepositoryImpl implements ElasticsearchCatalogDocumentRepositoryCustom {

    private static final List<String> SEARCH_FIELDS = List.of("title^3", "subtitle^2", "author^2");
    private static final String FUZZINESS_AUTO = "AUTO";

    private static final Logger log = LoggerFactory.getLogger(ElasticsearchCatalogDocumentRepositoryImpl.class);
    private final ElasticsearchOperations elasticsearchOperations;

    public ElasticsearchCatalogDocumentRepositoryImpl(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public Page<CatalogDocument> orSearch(List<String> keywords, Pageable pageable) {
        if (keywords == null || keywords.isEmpty()) {
            log.debug("Empty keywords provided for OR search");
            return Page.empty(pageable);
        }

        log.debug("Executing OR search with keywords: {}", keywords);

        List<Query> shouldQueries = keywords.stream()
                .map(this::createMultiMatchQuery)
                .toList();

        Query boolQuery = BoolQuery.of(b -> b
                .should(shouldQueries)
                .minimumShouldMatch("1")
        )._toQuery();

        NativeQuery searchQuery = buildSearchQuery(boolQuery, pageable);
        SearchHits<CatalogDocument> searchHits = executeElasticsearchQuery(searchQuery, "OR");

        return convertToPage(searchHits, pageable, "OR");
    }

    public Page<CatalogDocument> notSearch(List<String> includeKeywords, List<String> excludeKeywords, Pageable pageable) {
        if (includeKeywords == null || includeKeywords.isEmpty()) {
            log.debug("Empty include keywords provided for NOT search");
            return Page.empty(pageable);
        }

        log.debug("Executing NOT search with include keywords: {}, exclude keywords: {}", includeKeywords, excludeKeywords);

        List<Query> mustQueries = includeKeywords.stream()
                .map(this::createMultiMatchQuery)
                .toList();

        List<Query> mustNotQueries = (excludeKeywords != null && !excludeKeywords.isEmpty())
                ? excludeKeywords.stream()
                    .map(this::createMultiMatchQuery)
                    .toList()
                : List.of();

        Query boolQuery = BoolQuery.of(b -> b
                .must(mustQueries)
                .mustNot(mustNotQueries)
        )._toQuery();

        NativeQuery searchQuery = buildSearchQuery(boolQuery, pageable);
        SearchHits<CatalogDocument> searchHits = executeElasticsearchQuery(searchQuery, "NOT");

        return convertToPage(searchHits, pageable, "NOT");
    }

    private Query createMultiMatchQuery(String keyword) {
        return MultiMatchQuery.of(m -> m
                .query(keyword)
                .fields(SEARCH_FIELDS)
                .type(TextQueryType.BestFields)
                .fuzziness(FUZZINESS_AUTO)
        )._toQuery();
    }

    private NativeQuery buildSearchQuery(Query boolQuery, Pageable pageable) {
        return NativeQuery.builder()
                .withQuery(boolQuery)
                .withPageable(pageable)
                .build();
    }

    private SearchHits<CatalogDocument> executeElasticsearchQuery(NativeQuery searchQuery, String operation) {
        try {
            return elasticsearchOperations.search(searchQuery, CatalogDocument.class);
        } catch (Exception e) {
            log.error("Elasticsearch search failed for {} operation", operation, e);
            throw new SearchOperationException(operation + " 검색 중 오류가 발생했습니다", e);
        }
    }

    private Page<CatalogDocument> convertToPage(SearchHits<CatalogDocument> searchHits, Pageable pageable, String operation) {
        List<CatalogDocument> documents = searchHits.stream()
                .map(SearchHit::getContent)
                .toList();

        log.debug("{} search completed. Found {} total hits", operation, searchHits.getTotalHits());
        return PageableExecutionUtils.getPage(documents, pageable, searchHits::getTotalHits);
    }
}
