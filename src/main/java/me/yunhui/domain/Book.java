package me.yunhui.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Book {

    private final BookId id;
    private final String title;
    private final String subtitle;
    private final String author;
    private final String isbn;
    private final String publisher;
    private final LocalDate published;
    private final String imageUrl;
    private final String description;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public Book(BookId id, String title, String author, String isbn) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.subtitle = null;
        this.publisher = null;
        this.published = null;
        this.imageUrl = null;
        this.description = null;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Book(BookId id, String title, String subtitle, String author, String isbn, 
                String publisher, LocalDate published, String imageUrl, String description) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.author = author;
        this.isbn = isbn;
        this.publisher = publisher;
        this.published = published;
        this.imageUrl = imageUrl;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public BookId getId() {
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

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                '}';
    }
}