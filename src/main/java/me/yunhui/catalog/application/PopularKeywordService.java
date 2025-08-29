package me.yunhui.catalog.application;

import me.yunhui.catalog.domain.repository.CatalogKeywordRepository;
import me.yunhui.catalog.domain.vo.CatalogKeyword;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PopularKeywordService {
    
    private final CatalogKeywordRepository catalogKeywordRepository;
    
    public PopularKeywordService(CatalogKeywordRepository catalogKeywordRepository) {
        this.catalogKeywordRepository = catalogKeywordRepository;
    }
    
    public List<CatalogKeyword> getTopPopularKeywords(int limit) {
        return catalogKeywordRepository.findTopPopularKeywords(limit);
    }
    
    public List<CatalogKeyword> getTop10PopularKeywords() {
        return getTopPopularKeywords(10);
    }
}