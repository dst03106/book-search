package me.yunhui.catalog.domain.vo;

import me.yunhui.catalog.domain.entity.CatalogParsedQuery;
import me.yunhui.catalog.domain.exception.EmptyKeywordException;
import me.yunhui.catalog.domain.exception.InvalidPageNumberException;
import me.yunhui.catalog.domain.exception.InvalidPageSizeException;
import me.yunhui.catalog.domain.service.QueryParser;
import me.yunhui.shared.domain.ValueObject;
import java.util.List;
import java.util.Objects;

public class CatalogQuery extends ValueObject {
    
    private final String query;
    private final int page;
    private final int size;

    private CatalogQuery(String query, int page, int size) {
        if (query == null || query.isBlank()) {
            throw new EmptyKeywordException("검색어는 필수입니다");
        }
        if (page < 0) {
            throw new InvalidPageNumberException("페이지 번호는 0 이상이어야 합니다");
        }
        if (size <= 0) {
            throw new InvalidPageSizeException("페이지 크기는 1 이상이어야 합니다");
        }
        this.query = query.trim();
        this.page = page;
        this.size = size;
    }

    public static CatalogQuery of(String query, int page, int size) {
        return new CatalogQuery(query, page, size);
    }

    public String getQuery() {
        return query;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }
    
    public List<CatalogKeyword> extractIncludedKeywords() {
        QueryParser queryParser = new QueryParser();
        CatalogParsedQuery parsedQuery = queryParser.parse(this.query);
        
        return parsedQuery.getKeywords().stream()
                .filter(CatalogKeyword::isIncludedInSearch)
                .toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CatalogQuery that = (CatalogQuery) o;
        return page == that.page &&
               size == that.size &&
               Objects.equals(query, that.query);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, page, size);
    }

    @Override
    public String toString() {
        return "CatalogQuery{" +
                "query='" + query + '\'' +
                ", page=" + page +
                ", size=" + size +
                '}';
    }
}

