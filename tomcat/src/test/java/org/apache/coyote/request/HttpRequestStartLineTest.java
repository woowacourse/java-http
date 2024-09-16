package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestStartLineTest {

    @DisplayName("Start Line이 존재하면 정상적으로 생성된다.")
    @Test
    void createSuccess() {
        assertThatCode(() -> HttpRequestStartLine.from("GET /index.html HTTP/1.1"))
                .doesNotThrowAnyException();
    }

    @DisplayName("Start Line이 비어있으면 예외가 발생한다.")
    @Test
    void createFailByBlank() {
        assertThatThrownBy(() -> HttpRequestStartLine.from(" "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("Start Line이 Null이면 예외가 발생한다.")
    @Test
    void createFailByNull() {
        assertThatThrownBy(() -> HttpRequestStartLine.from(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("생성된 Start Line에서 http 요청 메서드, 타겟, http 버전을 추출한다.")
    @Test
    void extractStartLine() {
        HttpRequestStartLine requestStartLine = HttpRequestStartLine.from("GET /index.html?name=kaki HTTP/1.1");

        assertAll(
                () -> assertThat(requestStartLine.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(requestStartLine.getTargetPath()).isEqualTo("/index.html"),
                () -> assertThat(requestStartLine.getQueryParameters().getValueBy("name")).isEqualTo("kaki"),
                () -> assertThat(requestStartLine.getVersion()).isEqualTo(HttpVersion.HTTP1_1)
        );
    }
}
