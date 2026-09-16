package org.apache.coyote.http;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class ContentTypeTest {

    @ParameterizedTest
    @CsvSource({
            "application/json, JSON",
            "text/plain, TEXT_PLAIN",
            "APPLICATION/JSON, JSON",                       // 대소문자를 구분하지 않는다
            "application/json; charset=UTF-8, JSON",        // 파라미터를 제외하고 판단한다
            "text/plain ; charset=UTF-8, TEXT_PLAIN"        // 파라미터 앞 공백을 제거한다
    })
    void 컨텐츠_타입을_생성한다(final String headerValue, final ContentType expected) {
        assertThat(ContentType.from(headerValue)).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "text/html",        // 지원하지 않는 타입
            "JSON",             // enum 이름
            ""                  // Content-Type 헤더가 없는 경우
    })
    void 지원하지_않는_컨텐츠_타입은_UNKNOWN_이다(final String headerValue) {
        assertThat(ContentType.from(headerValue)).isEqualTo(ContentType.UNKNOWN);
    }
}
