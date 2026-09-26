package org.apache.coyote.http11;

import org.apache.coyote.http11.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PercentDecoderTest {
    @Test
    void 한글을_디코딩한다() {
        assertThat(PercentDecoder.decodePath("/%ED%95%9C%EA%B8%80")).isEqualTo("/한글");
    }

    @Test
    void 경로에서는_플러스를_보존한다() {
        assertThat(PercentDecoder.decodePath("/c++")).isEqualTo("/c++");
    }

    @Test
    void form에서는_플러스를_공백으로_바꾼다() {
        assertThat(PercentDecoder.decodeForm("hello+world")).isEqualTo("hello world");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "%ZZ",        // 1단계: hex 아님
            "%4",         // 1단계: 잘림
            "%",          // 1단계: 잘림
            "%٣F",        // 1단계: 유니코드 숫자
            "%FF",        // 2단계: UTF-8에 없는 바이트
            "%C3",        // 2단계: 잘린 멀티바이트
            "%C0%AE",     // 2단계: overlong '.'
            "한",         // 인코딩 안 된 비ASCII
    })
    void 잘못된_인코딩은_거부한다(final String raw) {
        assertThatThrownBy(() -> PercentDecoder.decodePath(raw))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void form에서도_인코딩된_플러스는_플러스로_디코딩한다() {
        assertThat(PercentDecoder.decodeForm("1%2B1")).isEqualTo("1+1");
    }

    @Test
    void 경로에서_인코딩된_플러스도_플러스로_디코딩한다() {
        assertThat(PercentDecoder.decodePath("/c%2B%2B")).isEqualTo("/c++");
    }

    @Test
    void 소문자_hex도_디코딩한다() {
        assertThat(PercentDecoder.decodePath("/%ed%95%9c")).isEqualTo("/한");
    }

    @Test
    void 빈_문자열은_빈_문자열이다() {
        assertThat(PercentDecoder.decodePath("")).isEmpty();
    }

    @Test
    void 제어_문자_검증은_하지_않는다() {
        // 내용 검증은 Path, QueryParameters의 책임
        assertThat(PercentDecoder.decodePath("%0A")).isEqualTo("\n");
    }

    @ParameterizedTest
    @ValueSource(strings = {"%ZZ", "%FF", "한"})
    void form_디코딩도_잘못된_인코딩을_거부한다(final String raw) {
        assertThatThrownBy(() -> PercentDecoder.decodeForm(raw))
                .isInstanceOf(BadRequestException.class);
    }
}