package me.yunhui.catalog.infrastructure.elasticsearch;

import me.yunhui.catalog.domain.entity.CatalogItem;
import me.yunhui.catalog.domain.vo.CatalogResult;
import org.springframework.stereotype.Component;

@Component
public class CatalogMapper {

    public CatalogDocument toDocument(CatalogItem item) {
        return new CatalogDocument(
                item.id(),
                item.title(),
                item.subtitle(),
                item.author(),
                item.isbn(),
                item.publisher(),
                item.published(),
                item.imageUrl(),
                item.createdAt(),
                item.updatedAt()
        );
    }

    public CatalogItem toDomain(CatalogDocument document) {
        return new CatalogItem(
                document.getId(),
                document.getTitle(),
                document.getSubtitle(),
                document.getAuthor(),
                document.getIsbn(),
                document.getPublisher(),
                document.getPublished(),
                document.getImageUrl(),
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }

    public CatalogResult.CatalogItemResult toResultItem(CatalogDocument document) {
        return new CatalogResult.CatalogItemResult(
                document.getId(),
                document.getTitle(),
                document.getSubtitle(),
                document.getAuthor(),
                document.getIsbn(),
                document.getPublisher(),
                document.getPublished(),
                document.getImageUrl()
        );
    }
}
