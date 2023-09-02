package org.apache.coyote.http11.request;

import org.apache.coyote.http11.common.HttpMethod;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class HttpRequestTest {

    private final BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);
    private final InputStreamReader inputStreamReader = Mockito.mock(InputStreamReader.class);

    @Test
    void 입력받은_정보로_요청_uri를_반환한다() throws IOException {
        // given
        String request = "GET /index.html HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Accept: */*\r\n" +
                "User-Agent: curl/7.64.1\r\n" +
                "\r\n";
        InputStream mockInputStream = new ByteArrayInputStream(request.getBytes());

        when(bufferedReader.readLine()).thenReturn(request, null);
        when(inputStreamReader.read()).thenReturn(-1);

        //when
        HttpRequest parsedRequest = HttpRequest.parse(mockInputStream);
        String requestUri = parsedRequest.getRequestUri();

        // then
        assertThat(requestUri).isEqualTo("/index.html");
    }

    @Test
    void 입력받은_정보로_http_method를_반환한다() throws IOException {
        // given
        String request = "GET /index.html HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Accept: */*\r\n" +
                "User-Agent: curl/7.64.1\r\n" +
                "\r\n";
        InputStream mockInputStream = new ByteArrayInputStream(request.getBytes());

        // when
        HttpRequest parsedRequest = HttpRequest.parse(mockInputStream);
        HttpMethod requestMethod = parsedRequest.getMethod();

        // then
        assertThat(requestMethod).isEqualTo(HttpMethod.GET);
    }

    @Test
    void 입력받은_정보로_쿼리_파라미터를_반환한다() throws IOException {
        // given
        String request = "GET /user/create?account=gugu&password=password HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Accept: */*\r\n" +
                "User-Agent: curl/7.64.1\r\n" +
                "\r\n";
        InputStream mockInputStream = new ByteArrayInputStream(request.getBytes());

        // when
        HttpRequest parsedRequest = HttpRequest.parse(mockInputStream);

        // then
        assertSoftly(softly -> {
            softly.assertThat(parsedRequest.getQueryParam("account")).isEqualTo("gugu");
            softly.assertThat(parsedRequest.getQueryParam("password")).isEqualTo("password");
        });
    }

    @Test
    void 입력받은_정보로_요청_헤더를_반환한다() throws IOException {
        // given
        String request = "GET /index.html HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Accept: */*\r\n" +
                "User-Agent: curl/7.64.1\r\n" +
                "\r\n";
        InputStream mockInputStream = new ByteArrayInputStream(request.getBytes());

        // when
        HttpRequest parsedRequest = HttpRequest.parse(mockInputStream);
        RequestHeaders requestHeaders = parsedRequest.getRequestHeaders();

        // then
        assertSoftly(softly -> {
            softly.assertThat(requestHeaders.getHeader("Host")).isEqualTo("localhost:8080");
            softly.assertThat(requestHeaders.getHeader("Connection")).isEqualTo("keep-alive");
            softly.assertThat(requestHeaders.getHeader("Accept")).isEqualTo("*/*");
            softly.assertThat(requestHeaders.getHeader("User-Agent")).isEqualTo("curl/7.64.1");
        });
    }
}
