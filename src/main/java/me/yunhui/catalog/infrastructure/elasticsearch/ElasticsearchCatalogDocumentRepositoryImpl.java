package me.yunhui.catalog.infrastructure.elasticsearch;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ElasticsearchCatalogDocumentRepositoryImpl implements ElasticsearchCatalogDocumentRepositoryCustom {

    private final ElasticsearchOperations elasticsearchOperations;

    public ElasticsearchCatalogDocumentRepositoryImpl(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public Page<CatalogDocument> orSearch(List<String> keywords, Pageable pageable) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        for (String keyword : keywords) {
            MultiMatchQueryBuilder multiMatchQuery = QueryBuilders.multiMatchQuery(keyword)
                    .field("title", 3.0f)
                    .field("subtitle", 2.0f)
                    .field("author", 2.0f)
                    .type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
                    .fuzziness("AUTO");
            boolQuery.should(multiMatchQuery);
        }

        boolQuery.minimumShouldMatch(1);

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withPageable(pageable)
                .build();

        SearchHits<CatalogDocument> searchHits = elasticsearchOperations.search(searchQuery, CatalogDocument.class);

        List<CatalogDocument> documents = searchHits.stream()
                .map(SearchHit::getContent)
                .toList();

        return PageableExecutionUtils.getPage(documents, pageable, searchHits::getTotalHits);
    }

    public Page<CatalogDocument> notSearch(List<String> includeKeywords, List<String> excludeKeywords, Pageable pageable) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // Must clauses for include keywords
        for (String keyword : includeKeywords) {
            MultiMatchQueryBuilder multiMatchQuery = QueryBuilders.multiMatchQuery(keyword)
                    .field("title", 3.0f)
                    .field("subtitle", 2.0f)
                    .field("author", 2.0f)
                    .type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
                    .fuzziness("AUTO");
            boolQuery.must(multiMatchQuery);
        }

        // Must not clauses for exclude keywords
        for (String keyword : excludeKeywords) {
            MultiMatchQueryBuilder multiMatchQuery = QueryBuilders.multiMatchQuery(keyword)
                    .field("title", 3.0f)
                    .field("subtitle", 2.0f)
                    .field("author", 2.0f)
                    .type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
                    .fuzziness("AUTO");
            boolQuery.mustNot(multiMatchQuery);
        }

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withPageable(pageable)
                .build();

        SearchHits<CatalogDocument> searchHits = elasticsearchOperations.search(searchQuery, CatalogDocument.class);

        List<CatalogDocument> documents = searchHits.stream()
                .map(SearchHit::getContent)
                .toList();

        return PageableExecutionUtils.getPage(documents, pageable, searchHits::getTotalHits);
    }
}