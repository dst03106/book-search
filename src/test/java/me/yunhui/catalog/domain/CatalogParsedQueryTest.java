package me.yunhui.catalog.domain;

import me.yunhui.catalog.domain.exception.EmptyKeywordException;
import me.yunhui.catalog.domain.vo.CatalogParsedQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("CatalogParsedQuery 테스트")
class CatalogParsedQueryTest {
    
    @Nested
    @DisplayName("단순 쿼리 생성")
    class SimpleQueryCreation {
        
        @Test
        @DisplayName("단순 키워드로 CatalogParsedQuery를 생성할 수 있다")
        void createSimpleQuery() {
            CatalogParsedQuery query = CatalogParsedQuery.simple("java");
            
            assertThat(query.getType()).isEqualTo(CatalogParsedQuery.QueryType.SIMPLE);
            assertThat(query.getFirstKeyword()).isEqualTo("java");
            assertThat(query.getSecondKeyword()).isNull();
            assertThat(query.isSimple()).isTrue();
            assertThat(query.isOr()).isFalse();
            assertThat(query.isNot()).isFalse();
        }
        
        @Test
        @DisplayName("공백이 포함된 키워드는 트림된다")
        void trimWhitespace() {
            CatalogParsedQuery query = CatalogParsedQuery.simple("  spring boot  ");
            
            assertThat(query.getFirstKeyword()).isEqualTo("spring boot");
        }
    }
    
    @Nested
    @DisplayName("OR 쿼리 생성")
    class OrQueryCreation {
        
        @Test
        @DisplayName("OR 키워드로 CatalogParsedQuery를 생성할 수 있다")
        void createOrQuery() {
            CatalogParsedQuery query = CatalogParsedQuery.or("java", "kotlin", "java|kotlin");
            
            assertThat(query.getType()).isEqualTo(CatalogParsedQuery.QueryType.OR);
            assertThat(query.getFirstKeyword()).isEqualTo("java");
            assertThat(query.getSecondKeyword()).isEqualTo("kotlin");
            assertThat(query.getOriginalQuery()).isEqualTo("java|kotlin");
            assertThat(query.isOr()).isTrue();
            assertThat(query.isSimple()).isFalse();
            assertThat(query.isNot()).isFalse();
        }
    }
    
    @Nested
    @DisplayName("NOT 쿼리 생성")
    class NotQueryCreation {
        
        @Test
        @DisplayName("NOT 키워드로 CatalogParsedQuery를 생성할 수 있다")
        void createNotQuery() {
            CatalogParsedQuery query = CatalogParsedQuery.not("programming", "beginner", "programming-beginner");
            
            assertThat(query.getType()).isEqualTo(CatalogParsedQuery.QueryType.NOT);
            assertThat(query.getFirstKeyword()).isEqualTo("programming");
            assertThat(query.getSecondKeyword()).isEqualTo("beginner");
            assertThat(query.getOriginalQuery()).isEqualTo("programming-beginner");
            assertThat(query.isNot()).isTrue();
            assertThat(query.isSimple()).isFalse();
            assertThat(query.isOr()).isFalse();
        }
    }
    
    @Nested
    @DisplayName("유효성 검사")
    class Validation {
        
        @Test
        @DisplayName("키워드가 null이면 예외를 발생시킨다")
        void nullKeywords() {
            assertThatThrownBy(() -> CatalogParsedQuery.simple(null))
                    .isInstanceOf(EmptyKeywordException.class)
                    .hasMessage("Keyword cannot be null or empty");
        }
        
        @Test
        @DisplayName("빈 키워드 리스트는 허용되지 않는다")
        void emptyKeywords() {
            assertThatThrownBy(() -> CatalogParsedQuery.simple(""))
                    .isInstanceOf(EmptyKeywordException.class)
                    .hasMessage("Keyword cannot be null or empty");
        }
    }
    
    @Nested
    @DisplayName("동등성 비교")
    class Equality {
        
        @Test
        @DisplayName("같은 내용의 CatalogParsedQuery는 동등하다")
        void equality() {
            CatalogParsedQuery query1 = CatalogParsedQuery.simple("java");
            CatalogParsedQuery query2 = CatalogParsedQuery.simple("java");
            
            assertThat(query1).isEqualTo(query2);
            assertThat(query1.hashCode()).isEqualTo(query2.hashCode());
        }
        
        @Test
        @DisplayName("다른 내용의 CatalogParsedQuery는 동등하지 않다")
        void inequality() {
            CatalogParsedQuery query1 = CatalogParsedQuery.simple("java");
            CatalogParsedQuery query2 = CatalogParsedQuery.simple("kotlin");
            
            assertThat(query1).isNotEqualTo(query2);
        }
    }
}
