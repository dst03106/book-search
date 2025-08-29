package me.yunhui.catalog.domain.service;

import me.yunhui.catalog.domain.vo.Pagination;
import me.yunhui.catalog.domain.vo.CatalogParsedQuery;
import me.yunhui.catalog.domain.vo.CatalogQueryResult;

public interface SearchStrategy {
    CatalogQueryResult search(CatalogParsedQuery parsedQuery, Pagination pagination);
    boolean supports(CatalogParsedQuery.QueryType queryType);
}
