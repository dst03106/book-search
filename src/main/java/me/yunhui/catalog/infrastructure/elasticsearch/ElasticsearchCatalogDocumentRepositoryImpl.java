package me.yunhui.catalog.infrastructure.elasticsearch;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
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
        List<Query> shouldQueries = keywords.stream()
                .map(keyword -> MultiMatchQuery.of(m -> m
                        .query(keyword)
                        .fields("title^3", "subtitle^2", "author^2")
                        .type(co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType.BestFields)
                        .fuzziness("AUTO")
                )._toQuery())
                .toList();

        Query boolQuery = BoolQuery.of(b -> b
                .should(shouldQueries)
                .minimumShouldMatch("1")
        )._toQuery();

        NativeQuery searchQuery = NativeQuery.builder()
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
        List<Query> mustQueries = includeKeywords.stream()
                .map(keyword -> MultiMatchQuery.of(m -> m
                        .query(keyword)
                        .fields("title^3", "subtitle^2", "author^2")
                        .type(co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType.BestFields)
                        .fuzziness("AUTO")
                )._toQuery())
                .toList();

        List<Query> mustNotQueries = excludeKeywords.stream()
                .map(keyword -> MultiMatchQuery.of(m -> m
                        .query(keyword)
                        .fields("title^3", "subtitle^2", "author^2")
                        .type(co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType.BestFields)
                        .fuzziness("AUTO")
                )._toQuery())
                .toList();

        Query boolQuery = BoolQuery.of(b -> b
                .must(mustQueries)
                .mustNot(mustNotQueries)
        )._toQuery();

        NativeQuery searchQuery = NativeQuery.builder()
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