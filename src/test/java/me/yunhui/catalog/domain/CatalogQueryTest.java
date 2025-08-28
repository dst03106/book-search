package me.yunhui.catalog.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("CatalogQuery 테스트")
class CatalogQueryTest {
    
    @Test
    @DisplayName("유효한 쿼리로 CatalogQuery를 생성할 수 있다")
    void createValidCatalogQuery() {
        CatalogQuery query = CatalogQuery.of("java spring", 0, 10);
        
        assertThat(query.getQuery()).isEqualTo("java spring");
        assertThat(query.getPage()).isEqualTo(0);
        assertThat(query.getSize()).isEqualTo(10);
    }
    
    @Test
    @DisplayName("null 또는 빈 쿼리는 예외를 발생시킨다")
    void nullOrBlankQueryThrowsException() {
        assertThatThrownBy(() -> CatalogQuery.of(null, 0, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Query cannot be null or blank");
                
        assertThatThrownBy(() -> CatalogQuery.of("", 0, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Query cannot be null or blank");
                
        assertThatThrownBy(() -> CatalogQuery.of("   ", 0, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Query cannot be null or blank");
    }
    
    @Test
    @DisplayName("음수 페이지는 예외를 발생시킨다")
    void negativePageThrowsException() {
        assertThatThrownBy(() -> CatalogQuery.of("java", -1, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Page cannot be negative");
    }
    
    @Test
    @DisplayName("0 이하의 사이즈는 예외를 발생시킨다")
    void nonPositiveSizeThrowsException() {
        assertThatThrownBy(() -> CatalogQuery.of("java", 0, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Size must be positive");
                
        assertThatThrownBy(() -> CatalogQuery.of("java", 0, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Size must be positive");
    }
    
    @Test
    @DisplayName("쿼리 문자열의 앞뒤 공백은 제거된다")
    void queryStringIsTrimmed() {
        CatalogQuery query = CatalogQuery.of("  java spring  ", 0, 10);
        
        assertThat(query.getQuery()).isEqualTo("java spring");
    }
}
