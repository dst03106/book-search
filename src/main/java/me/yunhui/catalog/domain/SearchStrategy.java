package me.yunhui.catalog.domain;

public interface SearchStrategy {
    SearchResult search(ParsedQuery parsedQuery, Pagination pagination);
    boolean supports(ParsedQuery.QueryType queryType);
}
