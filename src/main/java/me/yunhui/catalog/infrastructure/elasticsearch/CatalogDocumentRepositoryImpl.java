package me.yunhui.catalog.infrastructure.elasticsearch;

import me.yunhui.catalog.domain.entity.CatalogItem;
import me.yunhui.catalog.domain.repository.CatalogDocumentRepository;
import me.yunhui.catalog.domain.vo.Pagination;
import me.yunhui.catalog.domain.vo.CatalogQueryResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;

@Component
public class CatalogDocumentRepositoryImpl implements CatalogDocumentRepository {
    
    private final ElasticsearchCatalogDocumentRepository elasticsearchRepository;
    private final CatalogMapper mapper;
    
    public CatalogDocumentRepositoryImpl(ElasticsearchCatalogDocumentRepository elasticsearchRepository, CatalogMapper mapper) {
        this.elasticsearchRepository = elasticsearchRepository;
        this.mapper = mapper;
    }
    
    @Override
    public CatalogQueryResult smartSearch(String query, Pagination pagination) {
        Page<CatalogDocument> page = elasticsearchRepository.smartSearch(
            query, 
            PageRequest.of(pagination.page(), pagination.size())
        );
        
        List<CatalogItem> items = page.getContent()
                .stream()
                .map(mapper::toDomain)
                .toList();
        
        return new CatalogQueryResult(items, page.getTotalElements());
    }
    
    @Override
    public CatalogQueryResult orSearch(String keyword1, String keyword2, Pagination pagination) {
        Page<CatalogDocument> page = elasticsearchRepository.orSearch(
            keyword1, 
            keyword2,
            PageRequest.of(pagination.page(), pagination.size())
        );
        
        List<CatalogItem> items = page.getContent()
                .stream()
                .map(mapper::toDomain)
                .toList();
        
        return new CatalogQueryResult(items, page.getTotalElements());
    }
    
    @Override
    public CatalogQueryResult notSearch(String includeKeyword, String excludeKeyword, Pagination pagination) {
        Page<CatalogDocument> page = elasticsearchRepository.notSearch(
            includeKeyword, 
            excludeKeyword,
            PageRequest.of(pagination.page(), pagination.size())
        );
        
        List<CatalogItem> items = page.getContent()
                .stream()
                .map(mapper::toDomain)
                .toList();
        
        return new CatalogQueryResult(items, page.getTotalElements());
    }
    
    @Override
    public void saveAll(Iterable<CatalogItem> items) {
        List<CatalogDocument> documents = StreamSupport.stream(items.spliterator(), false)
                .map(mapper::toDocument)
                .toList();
        
        elasticsearchRepository.saveAll(documents);
    }
}
