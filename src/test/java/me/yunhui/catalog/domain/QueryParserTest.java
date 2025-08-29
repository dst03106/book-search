package me.yunhui.catalog.domain;

import me.yunhui.catalog.domain.exception.EmptyKeywordException;
import me.yunhui.catalog.domain.exception.InvalidQueryFormatException;
import me.yunhui.catalog.domain.service.QueryParser;
import me.yunhui.catalog.domain.entity.CatalogParsedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("QueryParser 테스트")
class QueryParserTest {
    
    private QueryParser queryParser;
    
    @BeforeEach
    void setUp() {
        queryParser = new QueryParser();
    }
    
    @Nested
    @DisplayName("단순 검색 쿼리")
    class SimpleQuery {
        
        @Test
        @DisplayName("단순 키워드를 파싱할 수 있다")
        void parseSimpleQuery() {
            CatalogParsedQuery parsedQuery = queryParser.parse("java");
            
            assertThat(parsedQuery.getKeywords()).hasSize(1);
            assertThat(parsedQuery.getFirstKeyword().value()).isEqualTo("java");
            assertThat(parsedQuery.getFirstKeyword().isIncludedInSearch()).isTrue();
            assertThat(parsedQuery.getSecondKeyword()).isNull();
        }
    }
    
    @Nested
    @DisplayName("OR 검색 쿼리")
    class OrQuery {
        
        @Test
        @DisplayName("OR 연산자를 포함한 쿼리를 파싱할 수 있다")
        void parseOrQuery() {
            CatalogParsedQuery parsedQuery = queryParser.parse("tdd|javascript");
            
            assertThat(parsedQuery.getKeywords()).hasSize(2);
            assertThat(parsedQuery.getFirstKeyword().value()).isEqualTo("tdd");
            assertThat(parsedQuery.getFirstKeyword().isIncludedInSearch()).isTrue();
            assertThat(parsedQuery.getSecondKeyword().value()).isEqualTo("javascript");
            assertThat(parsedQuery.getSecondKeyword().isIncludedInSearch()).isTrue();
            assertThat(parsedQuery.getOriginalQuery()).isEqualTo("tdd|javascript");
        }
        
        @Test
        @DisplayName("공백이 포함된 OR 쿼리를 올바르게 파싱한다")
        void parseOrQueryWithSpaces() {
            CatalogParsedQuery parsedQuery = queryParser.parse("spring boot | react");
            
            assertThat(parsedQuery.getKeywords()).hasSize(2);
            assertThat(parsedQuery.getFirstKeyword().value()).isEqualTo("spring boot");
            assertThat(parsedQuery.getFirstKeyword().isIncludedInSearch()).isTrue();
            assertThat(parsedQuery.getSecondKeyword().value()).isEqualTo("react");
            assertThat(parsedQuery.getSecondKeyword().isIncludedInSearch()).isTrue();
        }
        
        @Test
        @DisplayName("잘못된 OR 쿼리 형식은 예외를 발생시킨다")
        void invalidOrQueryFormat() {
            assertThatThrownBy(() -> queryParser.parse("java|"))
                    .isInstanceOf(EmptyKeywordException.class)
                    .hasMessage("OR query keywords cannot be empty");
        }
    }
    
    @Nested
    @DisplayName("NOT 검색 쿼리")
    class NotQuery {
        
        @Test
        @DisplayName("NOT 연산자를 포함한 쿼리를 파싱할 수 있다")
        void parseNotQuery() {
            CatalogParsedQuery parsedQuery = queryParser.parse("tdd-javascript");
            
            assertThat(parsedQuery.getKeywords()).hasSize(2);
            assertThat(parsedQuery.getFirstKeyword().value()).isEqualTo("tdd");
            assertThat(parsedQuery.getFirstKeyword().isIncludedInSearch()).isTrue();
            assertThat(parsedQuery.getSecondKeyword().value()).isEqualTo("javascript");
            assertThat(parsedQuery.getSecondKeyword().isIncludedInSearch()).isFalse();
            assertThat(parsedQuery.getOriginalQuery()).isEqualTo("tdd-javascript");
        }
        
        @Test
        @DisplayName("공백이 포함된 NOT 쿼리를 올바르게 파싱한다")
        void parseNotQueryWithSpaces() {
            CatalogParsedQuery parsedQuery = queryParser.parse("spring boot - legacy");
            
            assertThat(parsedQuery.getKeywords()).hasSize(2);
            assertThat(parsedQuery.getFirstKeyword().value()).isEqualTo("spring boot");
            assertThat(parsedQuery.getFirstKeyword().isIncludedInSearch()).isTrue();
            assertThat(parsedQuery.getSecondKeyword().value()).isEqualTo("legacy");
            assertThat(parsedQuery.getSecondKeyword().isIncludedInSearch()).isFalse();
        }
        
        @Test
        @DisplayName("잘못된 NOT 쿼리 형식은 예외를 발생시킨다")
        void invalidNotQueryFormat() {
            assertThatThrownBy(() -> queryParser.parse("java-"))
                    .isInstanceOf(EmptyKeywordException.class)
                    .hasMessage("NOT query keywords cannot be empty");
        }
    }
    
    @Nested
    @DisplayName("쿼리 우선순위")
    class QueryPriority {
        
        @Test
        @DisplayName("OR 연산자가 NOT 연산자보다 우선된다")
        void orHasPriorityOverNot() {
            CatalogParsedQuery parsedQuery = queryParser.parse("java|spring-boot");
            
            assertThat(parsedQuery.getKeywords()).hasSize(2);
            assertThat(parsedQuery.getFirstKeyword().value()).isEqualTo("java");
            assertThat(parsedQuery.getFirstKeyword().isIncludedInSearch()).isTrue();
            assertThat(parsedQuery.getSecondKeyword().value()).isEqualTo("spring-boot");
            assertThat(parsedQuery.getSecondKeyword().isIncludedInSearch()).isTrue();
        }
    }
    
    @Nested
    @DisplayName("예외 처리")
    class ExceptionHandling {
        
        @Test
        @DisplayName("null 쿼리는 예외를 발생시킨다")
        void nullQueryThrowsException() {
            assertThatThrownBy(() -> queryParser.parse(null))
                    .isInstanceOf(EmptyKeywordException.class)
                    .hasMessage("Query cannot be null or empty");
        }
        
        @Test
        @DisplayName("빈 쿼리는 예외를 발생시킨다")
        void emptyQueryThrowsException() {
            assertThatThrownBy(() -> queryParser.parse(""))
                    .isInstanceOf(EmptyKeywordException.class)
                    .hasMessage("Query cannot be null or empty");
        }
    }
}