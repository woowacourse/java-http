package org.apache.coyote.http11.request.startLine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @DisplayName("요청 라인이 null이거나 비어있으면 예외를 반환한다.")
    @Test
    void failureValidateRequestLineTest() {
        assertAll(
                () -> assertThatThrownBy(() -> new RequestLine(""))
                        .isInstanceOf(IllegalArgumentException.class),

                () -> assertThatThrownBy(() -> new RequestLine(null))
                        .isInstanceOf(IllegalArgumentException.class)
        );
    }

    @DisplayName("요청 라인을 파싱하는데 성공한다.")
    @Test
    void successSplitRequestLineTest() {
        String requestLine = "GET /static/jazz HTTP/1.1";

        RequestLine expected = new RequestLine(requestLine);

        assertAll(
                () -> assertThat(expected.getHttpMethod().getName()).isEqualTo(HttpMethod.GET.getName()),
                () -> assertThat(expected.getUri()).isEqualTo("/static/jazz"),
                () -> assertThat(expected.getHttpVersion().getHttpVersion()).isEqualTo("HTTP/1.1")
        );
    }


    @DisplayName("요청 라인의 구조가 올바르지 않으면 예외를 반환한다.")
    @Test
    void failureSplitRequestLineTest() {
        String requestLine = "GET /static/jazz";

        assertThatThrownBy(() -> new RequestLine(requestLine))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
