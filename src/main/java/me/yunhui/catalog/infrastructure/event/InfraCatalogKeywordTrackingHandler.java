package me.yunhui.catalog.infrastructure.event;

import me.yunhui.catalog.domain.event.CatalogKeywordSearchedEvent;
import me.yunhui.catalog.domain.handler.CatalogKeywordTrackingHandler;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class InfraCatalogKeywordTrackingHandler {
    
    private final CatalogKeywordTrackingHandler catalogKeywordTrackingHandler;
    
    public InfraCatalogKeywordTrackingHandler(CatalogKeywordTrackingHandler catalogKeywordTrackingHandler) {
        this.catalogKeywordTrackingHandler = catalogKeywordTrackingHandler;
    }
    
    @Async
    @EventListener
    public void handle(CatalogKeywordSearchedEvent event) {
        if (!event.searchedKeywords().isEmpty()) {
            try {
                catalogKeywordTrackingHandler.handle(event);
            } catch (Exception e) {
                // Redis 연결 실패 등의 경우 로그만 남기고 계속 진행
                System.err.println("Failed to track keyword: " + e.getMessage());
            }
        }
    }
}
