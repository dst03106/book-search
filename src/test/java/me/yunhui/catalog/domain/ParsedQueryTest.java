package me.yunhui.catalog.domain;

import me.yunhui.catalog.domain.exception.EmptyKeywordException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ParsedQuery 테스트")
class ParsedQueryTest {
    
    @Nested
    @DisplayName("단순 쿼리 생성")
    class SimpleQueryCreation {
        
        @Test
        @DisplayName("단순 키워드로 ParsedQuery를 생성할 수 있다")
        void createSimpleQuery() {
            ParsedQuery query = ParsedQuery.simple("java");
            
            assertThat(query.getType()).isEqualTo(ParsedQuery.QueryType.SIMPLE);
            assertThat(query.getFirstKeyword()).isEqualTo("java");
            assertThat(query.getSecondKeyword()).isNull();
            assertThat(query.isSimple()).isTrue();
            assertThat(query.isOr()).isFalse();
            assertThat(query.isNot()).isFalse();
        }
        
        @Test
        @DisplayName("공백이 포함된 키워드는 트림된다")
        void trimWhitespace() {
            ParsedQuery query = ParsedQuery.simple("  spring boot  ");
            
            assertThat(query.getFirstKeyword()).isEqualTo("spring boot");
        }
    }
    
    @Nested
    @DisplayName("OR 쿼리 생성")
    class OrQueryCreation {
        
        @Test
        @DisplayName("OR 키워드로 ParsedQuery를 생성할 수 있다")
        void createOrQuery() {
            ParsedQuery query = ParsedQuery.or("java", "kotlin", "java|kotlin");
            
            assertThat(query.getType()).isEqualTo(ParsedQuery.QueryType.OR);
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
        @DisplayName("NOT 키워드로 ParsedQuery를 생성할 수 있다")
        void createNotQuery() {
            ParsedQuery query = ParsedQuery.not("programming", "beginner", "programming-beginner");
            
            assertThat(query.getType()).isEqualTo(ParsedQuery.QueryType.NOT);
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
            assertThatThrownBy(() -> ParsedQuery.simple(null))
                    .isInstanceOf(EmptyKeywordException.class)
                    .hasMessage("Keyword cannot be null or empty");
        }
        
        @Test
        @DisplayName("빈 키워드 리스트는 허용되지 않는다")
        void emptyKeywords() {
            assertThatThrownBy(() -> ParsedQuery.simple(""))
                    .isInstanceOf(EmptyKeywordException.class)
                    .hasMessage("Keyword cannot be null or empty");
        }
    }
    
    @Nested
    @DisplayName("동등성 비교")
    class Equality {
        
        @Test
        @DisplayName("같은 내용의 ParsedQuery는 동등하다")
        void equality() {
            ParsedQuery query1 = ParsedQuery.simple("java");
            ParsedQuery query2 = ParsedQuery.simple("java");
            
            assertThat(query1).isEqualTo(query2);
            assertThat(query1.hashCode()).isEqualTo(query2.hashCode());
        }
        
        @Test
        @DisplayName("다른 내용의 ParsedQuery는 동등하지 않다")
        void inequality() {
            ParsedQuery query1 = ParsedQuery.simple("java");
            ParsedQuery query2 = ParsedQuery.simple("kotlin");
            
            assertThat(query1).isNotEqualTo(query2);
        }
    }
}
