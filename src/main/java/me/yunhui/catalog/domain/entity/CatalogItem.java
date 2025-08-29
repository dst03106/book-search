package me.yunhui.catalog.domain.entity;

import me.yunhui.shared.domain.AggregateRoot;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CatalogItem extends AggregateRoot<String> {
    private final String id;
    private final String title;
    private final String subtitle;
    private final String author;
    private final String isbn;
    private final String publisher;
    private final LocalDate published;
    private final String imageUrl;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    
    public CatalogItem(
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
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.author = author;
        this.isbn = isbn;
        this.publisher = publisher;
        this.published = published;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    public String id() { return id; }
    public String title() { return title; }
    public String subtitle() { return subtitle; }
    public String author() { return author; }
    public String isbn() { return isbn; }
    public String publisher() { return publisher; }
    public LocalDate published() { return published; }
    public String imageUrl() { return imageUrl; }
    public LocalDateTime createdAt() { return createdAt; }
    public LocalDateTime updatedAt() { return updatedAt; }
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
