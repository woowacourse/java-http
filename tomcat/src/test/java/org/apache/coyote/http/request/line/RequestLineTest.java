package org.apache.coyote.http.request.line;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http.HttpProtocol;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Http 요청 라인 테스트")
class RequestLineTest {

    @Test
    @DisplayName("RequestLine from 메서드는 HTTP 요청 라인 문자열을 파싱하여 RequestLine 객체를 생성한다.")
    void from() {
        // given
        String requestLine = "GET / HTTP/1.1";

        // when
        RequestLine line = RequestLine.from(requestLine);

        // then
        Assertions.assertAll(
                () -> assertThat(line.getMethod()).isEqualTo(Method.GET),
                () -> assertThat(line.getUriPath()).isEqualTo("/"),
                () -> assertThat(line.getProtocol()).isEqualTo(HttpProtocol.HTTP_11)
        );
    }

    @Test
    @DisplayName("주어진 메서드와 RequestLine 객체의 메서드를 비교해 일치 여부를 확인할 수 있다.")
    void isMethodTest() {
        // given
        RequestLine line = new RequestLine(Method.GET, new Uri("/"), HttpProtocol.HTTP_11);

        // when & then
        Assertions.assertAll(
                () -> assertThat(line.isMethod(Method.GET)).isTrue(),
                () -> assertThat(line.isMethod(Method.POST)).isFalse()
        );
    }

    @Test
    @DisplayName("isHttpProtocol는 주어진 프로토콜과 RequestLine 객체의 프로토콜을 비교해 일치 여부를 반환한다.")
    void isHttpProtocolTest() {
        // given
        RequestLine line = new RequestLine(Method.GET, new Uri("/"), HttpProtocol.HTTP_11);

        // when & then
        assertThat(line.isHttpProtocol(HttpProtocol.HTTP_11)).isTrue();
    }

    @Test
    @DisplayName("isUriHome은 RequestLine의 URI가 '/'인지 여부를 반환한다.")
    void isUriHomeTest() {
        // given
        RequestLine homeLine = new RequestLine(Method.GET, new Uri("/"), HttpProtocol.HTTP_11);
        RequestLine nonHomeLine = new RequestLine(Method.GET, new Uri("/test"), HttpProtocol.HTTP_11);

        // when & then
        Assertions.assertAll(
                () -> assertThat(homeLine.isUriHome()).isTrue(),
                () -> assertThat(nonHomeLine.isUriHome()).isFalse()
        );
    }
}
