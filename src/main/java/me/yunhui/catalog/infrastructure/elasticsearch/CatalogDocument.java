package me.yunhui.catalog.infrastructure.elasticsearch;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(indexName = "catalog")
public class CatalogDocument {

    @Id
    private final String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private final String title;

    @Field(type = FieldType.Text, analyzer = "standard")
    private final String subtitle;

    @Field(type = FieldType.Text, analyzer = "standard")
    private final String author;

    @Field(type = FieldType.Keyword)
    private final String isbn;

    @Field(type = FieldType.Text, analyzer = "standard")
    private final String publisher;

    @Field(type = FieldType.Date)
    private final LocalDate published;

    @Field(type = FieldType.Keyword)
    private final String imageUrl;

    @Field(type = FieldType.Date, format = {}, pattern ="uuuu-MM-dd'T'HH:mm:ss.SSSSSS")
    private final LocalDateTime createdAt;

    @Field(type = FieldType.Date, format = {}, pattern ="uuuu-MM-dd'T'HH:mm:ss.SSSSSS")
    private final LocalDateTime updatedAt;

    public CatalogDocument(String id, String title, String subtitle, String author, String isbn,
                          String publisher, LocalDate published, String imageUrl,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
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

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getPublisher() {
        return publisher;
    }

    public LocalDate getPublished() {
        return published;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
