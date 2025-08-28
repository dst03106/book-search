package me.yunhui.catalog.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CatalogItem(
    String id,
    String title,
    String subtitle,
    String author,
    String isbn,
    String publisher,
    LocalDate published,
    String imageUrl,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static CatalogItem create(
        String id,
        String title,
        String subtitle,
        String author,
        String isbn,
        String publisher,
        LocalDate published,
        String imageUrl
    ) {
        LocalDateTime now = LocalDateTime.now();
        return new CatalogItem(
            id, title, subtitle, author, isbn, publisher, 
            published, imageUrl, now, now
        );
    }
}
