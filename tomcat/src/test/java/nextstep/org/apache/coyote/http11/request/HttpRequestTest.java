package nextstep.org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HttpRequestTest {

    @DisplayName("Http 요청이 잘 바인딩 되는지 확인한다.")
    @Test
    void from() throws IOException {
        // given
        final String firstLine = "GET /index.html HTTP/1.1 ";
        final List<String> lines = List.of("Host: localhost:8080 ", "Connection: keep-alive ");

        final RequestLine requestLine = RequestLine.of(firstLine);

        // when
        final HttpRequest httpRequest = HttpRequest.from(firstLine, lines, null);

        // then
        assertAll(
                () -> assertThat(httpRequest.getHeaders().size()).isEqualTo(2),
                () -> assertThat(httpRequest.getRequestLine()).isEqualTo(requestLine)
        );
    }

    @DisplayName("Http 요청이 잘 바인딩 되는지 확인한다.")
    @Test
    void from_exist_body() throws IOException {
        // given
        final String firstLine = "POST /login HTTP/1.1 ";
        final List<String> lines = List.of("Host: localhost:8080 ", "Connection: keep-alive ", "Content-Length: 30");
        final String queryString = "account=gugu&password=password";
        final InputStream is = new ByteArrayInputStream(queryString.getBytes());
        final BufferedReader br = new BufferedReader(new InputStreamReader(is));

        final RequestLine requestLine = RequestLine.of(firstLine);

        // when
        final HttpRequest httpRequest = HttpRequest.from(firstLine, lines, br);

        // then
        assertAll(
                () -> assertThat(httpRequest.getHeaders().size()).isEqualTo(3),
                () -> assertThat(httpRequest.getRequestLine()).isEqualTo(requestLine),
                () -> assertThat(httpRequest.getBody().size()).isEqualTo(2)
        );
    }

    @DisplayName("Http 메서드가 GET 메서드 여부를 확인한다.")
    @Test
    void isGet() throws IOException {
        // given
        final String firstLine = "GET /index.html HTTP/1.1 ";
        final List<String> lines = List.of("Host: localhost:8080 ", "Connection: keep-alive ");

        // when
        final HttpRequest httpRequest = HttpRequest.from(firstLine, lines, null);

        // then
        assertTrue(httpRequest.isGet());
    }

    @DisplayName("요청 url을 확인한다.")
    @Test
    void getUrl() throws IOException {
        // given
        final String firstLine = "GET /index.html HTTP/1.1 ";
        final List<String> lines = List.of("Host: localhost:8080 ", "Connection: keep-alive ");

        // when
        final HttpRequest httpRequest = HttpRequest.from(firstLine, lines, null);

        // then
        assertThat(httpRequest.getUrl()).isEqualTo("/index.html");
    }
}
