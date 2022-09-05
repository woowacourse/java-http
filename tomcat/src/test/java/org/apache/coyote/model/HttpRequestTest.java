package org.apache.coyote.model;

import org.apache.coyote.exception.InvalidRequestFormat;
import org.apache.coyote.model.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpRequestTest {

    @Test
    @DisplayName("확장자가 없는 경우 html을 추가한다.")
    void checkNotExtensionAddDefaultExtension() {
        // given
        String requestLine = "GET /index HTTP/1.1";
        String expected = "/index.html";

        // when
        HttpRequest actual = HttpRequest.of(requestLine);

        // then
        assertThat(actual.getPath()).isEqualTo(expected);
    }

    @Test
    @DisplayName("param이 없는 경우 빈 값이 들어간다.")
    void checkNotParam() {
        // given
        String requestLine = "GET /index HTTP/1.1";

        // when
        HttpRequest actual = HttpRequest.of(requestLine);

        // then
        assertThat(actual.getParams()
                .isEmpty()).isTrue();
    }

    @Test
    @DisplayName("포맷이 맞지 않는 경우 예외가 발생한다.")
    void wrongRequestFormat() {
        // given
        String requestLine = "GET /index.html";

        // when & then
        assertThatThrownBy(() -> HttpRequest.of(requestLine))
                .isInstanceOf(InvalidRequestFormat.class);
    }

    @Test
    @DisplayName("HttpMethod를 검증한다.")
    void checkHttpMethod() {
        // given
        String requestLine = "POST /index HTTP/1.1";

        // when
        HttpRequest actual = HttpRequest.of(requestLine);

        // then
        assertThat(actual.getHttpMethod()).isEqualTo(HttpMethod.POST);
    }
}
