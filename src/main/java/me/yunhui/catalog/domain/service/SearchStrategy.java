package me.yunhui.catalog.domain.service;

import me.yunhui.catalog.domain.vo.Pagination;
import me.yunhui.catalog.domain.vo.ParsedQuery;
import me.yunhui.catalog.domain.vo.CatalogQueryResult;

public interface SearchStrategy {
    CatalogQueryResult search(ParsedQuery parsedQuery, Pagination pagination);
    boolean supports(ParsedQuery.QueryType queryType);
}
