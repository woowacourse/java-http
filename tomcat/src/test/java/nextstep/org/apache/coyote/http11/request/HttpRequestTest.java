package nextstep.org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HttpRequestTest {

    @DisplayName("Http 요청이 잘 바인딩 되는지 확인한다.")
    @Test
    void from() {
        // given
        final String firstLine = "GET /index.html HTTP/1.1 ";
        final List<String> lines = List.of("Host: localhost:8080 ", "Connection: keep-alive ", "");

        final RequestLine requestLine = RequestLine.of(firstLine);

        // when
        final HttpRequest httpRequest = HttpRequest.from(firstLine, lines);

        // then
        assertAll(
                () -> assertThat(httpRequest.getHeaders().size()).isEqualTo(2),
                () -> assertThat(httpRequest.getRequestLine()).isEqualTo(requestLine)
        );
    }

    @DisplayName("Http 요청이 잘 바인딩 되는지 확인한다.")
    @Test
    void from_exist_body() {
        // given
        final String firstLine = "POST /login HTTP/1.1 ";
        final List<String> lines = List.of("Host: localhost:8080 ", "Connection: keep-alive ", "",
                "account=gugu&password=password");

        final RequestLine requestLine = RequestLine.of(firstLine);

        // when
        final HttpRequest httpRequest = HttpRequest.from(firstLine, lines);

        // then
        assertAll(
                () -> assertThat(httpRequest.getHeaders().size()).isEqualTo(2),
                () -> assertThat(httpRequest.getRequestLine()).isEqualTo(requestLine),
                () -> assertThat(httpRequest.getBody().size()).isEqualTo(2)
        );
    }

    @DisplayName("Http 메서드가 GET 메서드 여부를 확인한다.")
    @Test
    void isGet() {
        // given
        final String firstLine = "GET /index.html HTTP/1.1 ";
        final List<String> lines = List.of("Host: localhost:8080 ", "Connection: keep-alive ", "");

        // when
        final HttpRequest httpRequest = HttpRequest.from(firstLine, lines);

        // then
        assertTrue(httpRequest.isGet());
    }

    @DisplayName("요청 url을 확인한다.")
    @Test
    void getUrl() {
        // given
        final String firstLine = "GET /index.html HTTP/1.1 ";
        final List<String> lines = List.of("Host: localhost:8080 ", "Connection: keep-alive ", "");

        // when
        final HttpRequest httpRequest = HttpRequest.from(firstLine, lines);

        // then
        assertThat(httpRequest.getUrl()).isEqualTo("/index.html");
    }
}
