package me.yunhui.catalog.infrastructure.elasticsearch;

import me.yunhui.catalog.domain.CatalogItem;
import me.yunhui.catalog.domain.CatalogQuery;
import me.yunhui.catalog.domain.CatalogRepository;
import me.yunhui.catalog.domain.CatalogResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;

@Component
public class CatalogRepositoryImpl implements CatalogRepository {
    
    private final CatalogDocumentRepository documentRepository;
    private final CatalogMapper mapper;
    
    public CatalogRepositoryImpl(CatalogDocumentRepository documentRepository, CatalogMapper mapper) {
        this.documentRepository = documentRepository;
        this.mapper = mapper;
    }
    
    @Override
    public CatalogResult search(CatalogQuery query) {
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize());
        String searchTerm = query.getQuery();
        
        // 모든 검색에 Smart Multi-Match 사용 (title^3, subtitle^2, author^2)
        Page<CatalogDocument> page = documentRepository.smartSearch(searchTerm, pageable);
        
        List<CatalogResult.CatalogItemResult> results = page.getContent()
                .stream()
                .map(mapper::toResultItem)
                .toList();
        
        return new CatalogResult(results, page.getTotalElements(), query.getPage(), query.getSize());
    }
    
    @Override
    public void saveAll(Iterable<CatalogItem> items) {
        List<CatalogDocument> documents = StreamSupport.stream(items.spliterator(), false)
                .map(mapper::toDocument)
                .toList();
        
        documentRepository.saveAll(documents);
    }
}
