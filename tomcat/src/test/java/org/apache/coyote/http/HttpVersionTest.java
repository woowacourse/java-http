package org.apache.coyote.http;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpVersionTest {

    @Test
    void HTTP_버전을_생성한다() {
        assertThat(HttpVersion.from("HTTP/1.1")).isEqualTo(HttpVersion.HTTP_1_1);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "HTTP_1_1",     // enum 이름
            "HTTP/1.0",     // 지원하지 않는 버전
            "http/1.1",     // 소문자
            "1.1"           // HTTP/ 접두사 없음
    })
    void 지원하지_않는_HTTP_버전은_예외가_발생한다(final String source) {
        assertThatThrownBy(() -> HttpVersion.from(source))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
