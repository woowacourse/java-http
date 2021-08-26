package nextstep.jwp.http.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HttpRequestParser 테스트")
class HttpRequestParserTest {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private final HttpRequestParser httpRequestParser = new HttpRequestParser();

    @DisplayName("static resource parsing 테스트")
    @Test
    void parseStaticResourceRequest() throws Exception {
        //given
        final String requestHeaders = String.join(NEW_LINE,
                "GET /index.html HTTP/1.1",
                "HOST: localhost:8080",
                "",
                "");

        //when
        final HttpRequest actualRequest;
        try (final InputStream inputStream = new ByteArrayInputStream(requestHeaders.getBytes())) {
            actualRequest = httpRequestParser.parse(inputStream);
        }
        final RequestLine actualRequestLine = actualRequest.getRequestLine();

        //then
        assertThat(actualRequestLine.hasMethod(HttpMethod.GET)).isTrue();
        assertThat(actualRequestLine.getUri()).isEqualTo("/index.html");
    }

    @DisplayName("GET uri path parsing 테스트")
    @Test
    void parseGetUriPathRequest() throws Exception {
        //given
        final String requestHeaders = String.join(NEW_LINE,
                "GET /login HTTP/1.1",
                "HOST: localhost:8080",
                "",
                "");

        //when
        final HttpRequest actualRequest;
        try (final InputStream inputStream = new ByteArrayInputStream(requestHeaders.getBytes())) {
            actualRequest = httpRequestParser.parse(inputStream);
        }
        final RequestLine actualRequestLine = actualRequest.getRequestLine();

        //then
        assertThat(actualRequestLine.hasMethod(HttpMethod.GET)).isTrue();
        assertThat(actualRequestLine.getUri()).isEqualTo("/login");
    }

    @DisplayName("POST uri path queryParam parsing 테스트")
    @Test
    void parsePOSTUriPathQueryParamRequest() throws Exception {
        //given
        final String requestBody = "account=inbi&password=1234";
        final int contentLength = requestBody.getBytes(StandardCharsets.UTF_8).length;
        final String requestHeaders = String.join(NEW_LINE,
                "POST /login HTTP/1.1",
                "HOST: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + contentLength,
                "",
                requestBody);

        //when
        final HttpRequest actualRequest;
        try (final InputStream inputStream = new ByteArrayInputStream(requestHeaders.getBytes())) {
            actualRequest = httpRequestParser.parse(inputStream);
        }
        final RequestLine actualRequestLine = actualRequest.getRequestLine();
        final RequestHeaders actualHeaders = actualRequest.getHeaders();
        final RequestBody actualBody = actualRequest.getBody();

        //then
        assertThat(actualRequestLine.hasMethod(HttpMethod.POST)).isTrue();
        assertThat(actualRequestLine.getUri()).isEqualTo("/login");
        assertThat(actualHeaders.getContentLength()).isEqualTo(contentLength);
        assertThat(actualBody.getParameter("account")).isEqualTo("inbi");
        assertThat(actualBody.getParameter("password")).isEqualTo("1234");
    }
}