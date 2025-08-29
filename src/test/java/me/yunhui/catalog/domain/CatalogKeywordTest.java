package me.yunhui.catalog.domain;

import me.yunhui.catalog.domain.exception.EmptyKeywordException;
import me.yunhui.catalog.domain.vo.CatalogKeyword;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("CatalogKeyword 도메인 모델 테스트")
class CatalogKeywordTest {

    @Test
    @DisplayName("포함 검색어 생성 시 정상적으로 생성된다")
    void createIncludedKeyword() {
        // given
        String keyword = "Java";
        
        // when
        CatalogKeyword catalogKeyword = CatalogKeyword.included(keyword);
        
        // then
        assertThat(catalogKeyword.value()).isEqualTo("java");
        assertThat(catalogKeyword.included()).isTrue();
        assertThat(catalogKeyword.isIncludedInSearch()).isTrue();
    }

    @Test
    @DisplayName("제외 검색어 생성 시 정상적으로 생성된다")
    void createExcludedKeyword() {
        // given
        String keyword = "JavaScript";
        
        // when
        CatalogKeyword catalogKeyword = CatalogKeyword.excluded(keyword);
        
        // then
        assertThat(catalogKeyword.value()).isEqualTo("javascript");
        assertThat(catalogKeyword.included()).isFalse();
        assertThat(catalogKeyword.isIncludedInSearch()).isFalse();
    }

    @Test
    @DisplayName("키워드 정규화가 정상적으로 동작한다")
    void normalizeKeyword() {
        // given & when
        CatalogKeyword keyword1 = new CatalogKeyword("  SPRING BOOT  ", true);
        CatalogKeyword keyword2 = CatalogKeyword.included("React.js");
        
        // then
        assertThat(keyword1.value()).isEqualTo("spring boot");
        assertThat(keyword2.value()).isEqualTo("react.js");
    }

    @ParameterizedTest
    @DisplayName("null이나 빈 문자열로 키워드 생성 시 예외가 발생한다")
    @NullAndEmptySource
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    void throwExceptionWhenKeywordIsNullOrEmpty(String invalidKeyword) {
        assertThatThrownBy(() -> new CatalogKeyword(invalidKeyword, true))
                .isInstanceOf(EmptyKeywordException.class)
                .hasMessage("Keyword cannot be null or empty");
    }

    @Test
    @DisplayName("toString은 키워드 값을 반환한다")
    void toStringReturnsKeywordValue() {
        // given
        CatalogKeyword keyword = CatalogKeyword.included("spring");
        
        // when & then
        assertThat(keyword.toString()).isEqualTo("spring");
    }

    @Test
    @DisplayName("동일한 키워드로 생성한 객체는 equals가 성립한다")
    void equalsWithSameKeyword() {
        // given
        CatalogKeyword keyword1 = CatalogKeyword.included("java");
        CatalogKeyword keyword2 = CatalogKeyword.included("JAVA");
        CatalogKeyword keyword3 = new CatalogKeyword("java", false);
        
        // then
        assertThat(keyword1).isEqualTo(keyword2);
        assertThat(keyword1).isNotEqualTo(keyword3); // included 값이 다름
    }
}