package me.yunhui.catalog.domain;

import me.yunhui.catalog.domain.exception.EmptyKeywordException;
import me.yunhui.catalog.domain.exception.InvalidPageNumberException;
import me.yunhui.catalog.domain.exception.InvalidPageSizeException;
import me.yunhui.catalog.domain.vo.CatalogQuery;
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
                .isInstanceOf(EmptyKeywordException.class)
                .hasMessage("검색어는 필수입니다");
                
        assertThatThrownBy(() -> CatalogQuery.of("", 0, 10))
                .isInstanceOf(EmptyKeywordException.class)
                .hasMessage("검색어는 필수입니다");
                
        assertThatThrownBy(() -> CatalogQuery.of("   ", 0, 10))
                .isInstanceOf(EmptyKeywordException.class)
                .hasMessage("검색어는 필수입니다");
    }
    
    @Test
    @DisplayName("음수 페이지는 예외를 발생시킨다")
    void negativePageThrowsException() {
        assertThatThrownBy(() -> CatalogQuery.of("java", -1, 10))
                .isInstanceOf(InvalidPageNumberException.class)
                .hasMessage("페이지 번호는 0 이상이어야 합니다");
    }
    
    @Test
    @DisplayName("0 이하의 사이즈는 예외를 발생시킨다")
    void nonPositiveSizeThrowsException() {
        assertThatThrownBy(() -> CatalogQuery.of("java", 0, 0))
                .isInstanceOf(InvalidPageSizeException.class)
                .hasMessage("페이지 크기는 1 이상이어야 합니다");
                
        assertThatThrownBy(() -> CatalogQuery.of("java", 0, -1))
                .isInstanceOf(InvalidPageSizeException.class)
                .hasMessage("페이지 크기는 1 이상이어야 합니다");
    }
    
    @Test
    @DisplayName("쿼리 문자열의 앞뒤 공백은 제거된다")
    void queryStringIsTrimmed() {
        CatalogQuery query = CatalogQuery.of("  java spring  ", 0, 10);
        
        assertThat(query.getQuery()).isEqualTo("java spring");
    }
}
