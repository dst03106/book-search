package me.yunhui.catalog.application;

import me.yunhui.catalog.domain.CatalogItem;
import me.yunhui.catalog.domain.CatalogRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DataImportService {
    
    private final CatalogRepository catalogRepository;
    
    public DataImportService(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }
    
    public void importCatalogItems(List<CatalogItem> items) {
        catalogRepository.saveAll(items);
    }
    
    public void clearAll() {
        // 전체 데이터 삭제는 요구사항에 없지만 테스트 시 필요할 수 있음
        // 현재는 Repository 인터페이스에 없으므로 주석 처리
        // catalogRepository.deleteAll();
    }
}
