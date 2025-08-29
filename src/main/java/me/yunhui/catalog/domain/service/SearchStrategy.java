package me.yunhui.catalog.domain.service;

import me.yunhui.catalog.domain.vo.Pagination;
import me.yunhui.catalog.domain.vo.ParsedQuery;
import me.yunhui.catalog.domain.vo.SearchResult;

public interface SearchStrategy {
    SearchResult search(ParsedQuery parsedQuery, Pagination pagination);
    boolean supports(ParsedQuery.QueryType queryType);
}
