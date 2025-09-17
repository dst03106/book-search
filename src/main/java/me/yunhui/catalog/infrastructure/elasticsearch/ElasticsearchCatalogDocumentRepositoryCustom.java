package me.yunhui.catalog.infrastructure.elasticsearch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ElasticsearchCatalogDocumentRepositoryCustom {

    /**
     * Searches for documents where any of the provided keywords match.
     * Uses OR logic - documents matching any keyword will be returned.
     *
     * @param keywords List of keywords to search for (OR logic)
     * @param pageable Pagination parameters
     * @return Page of matching documents
     */
    Page<CatalogDocument> orSearch(List<String> keywords, Pageable pageable);

    /**
     * Searches for documents that must contain all includeKeywords but must not contain any excludeKeywords.
     * Uses AND logic for includeKeywords and NOT logic for excludeKeywords.
     *
     * @param includeKeywords List of keywords that must be present (AND logic)
     * @param excludeKeywords List of keywords that must not be present (NOT logic)
     * @param pageable Pagination parameters
     * @return Page of matching documents
     */
    Page<CatalogDocument> notSearch(List<String> includeKeywords, List<String> excludeKeywords, Pageable pageable);
}