package org.apache.coyote.request;

import static org.apache.coyote.request.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.exception.UncheckedServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @DisplayName("생성 성공: query parameter 없음")
    @Test
    void construct_Success_NoQueryParameter() {
        RequestLine requestLine = new RequestLine("GET /test HTTP/1.1");

        assertAll(
                () -> assertThat(requestLine.getMethod()).isEqualTo(GET),
                () -> assertThat(requestLine.getPath()).isEqualTo("/test"),
                () -> assertThat(requestLine.getQueryParameters()).isEmpty()
        );
    }

    @DisplayName("생성 성공: query parameter 있음")
    @Test
    void construct_Success_WithQueryParameter() {
        RequestLine requestLine = new RequestLine("GET /test?name=lee&age=20 HTTP/1.1");

        assertAll(
                () -> assertThat(requestLine.getMethod()).isEqualTo(GET),
                () -> assertThat(requestLine.getPath()).isEqualTo("/test"),
                () -> assertThat(requestLine.getQueryParameters().get("name")).isEqualTo("lee"),
                () -> assertThat(requestLine.getQueryParameters().get("age")).isEqualTo("20")
        );
    }

    @DisplayName("생성 실패: 존재하지 않는 메서드")
    @Test
    void construct_Fail_IllegalMethod() {
        assertThatThrownBy(() -> new RequestLine("GETT /test HTTP/1.1"))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("올바르지 않은 HTTP Method입니다.");
    }

    @DisplayName("생성 실패: 올바르지 않은 URI")
    @Test
    void construct_Fail_IllegalUri() {
        assertThatThrownBy(() -> new RequestLine("GET test HTTP/1.1"))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("URI는 / 로 시작해야 합니다.");
    }

    @DisplayName("생성 실패: 올바르지 않은 HTTP Version")
    @Test
    void construct_Fail_IllegalHttpVersion() {
        assertThatThrownBy(() -> new RequestLine("GET /test HTTP/1.0"))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("HTTP 버전은 HTTP/1.1 만 허용됩니다.");
    }

    @DisplayName("생성 실패: 올바르지 않은 인자 개수")
    @Test
    void construct_Fail_IllegalParameterCount() {
        assertThatThrownBy(() -> new RequestLine("GET /test HTTP/1.1 HTTP/1.1"))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("Request line의 인자는 3개여야 합니다.");
    }
}
