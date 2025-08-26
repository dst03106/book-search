package me.yunhui.domain;

import java.util.Objects;
import java.util.UUID;

public class BookId {
    
    private final String value;

    private BookId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("BookId cannot be null or blank");
        }
        this.value = value;
    }

    public static BookId generate() {
        return new BookId(UUID.randomUUID().toString());
    }

    public static BookId of(String value) {
        return new BookId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookId bookId = (BookId) o;
        return Objects.equals(value, bookId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "BookId{" + "value='" + value + '\'' + '}';
    }
}