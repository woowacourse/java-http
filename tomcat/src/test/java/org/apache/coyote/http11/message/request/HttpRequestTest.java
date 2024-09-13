package org.apache.coyote.http11.message.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.StringReader;
import java.net.URI;
import java.util.Map;
import org.apache.coyote.http11.HttpCookies;
import org.apache.coyote.http11.message.common.ContentType;
import org.apache.coyote.http11.message.common.HttpBody;
import org.apache.coyote.http11.message.common.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    String body;
    private HttpRequest httpRequest;
    private String contentType;
    private int contentLength;
    private String cookie;

    @BeforeEach
    void setUp() {
        HttpRequestLine startLine = new HttpRequestLine("GET /path HTTP/1.1");

        contentType = "application/x-www-form-urlencoded";
        contentLength = 15;
        cookie = "sessionId=abc123";
        HttpHeaders headers = new HttpHeaders(
                "Content-Type: " + contentType + "\n"
                        + "Content-Length: " + contentLength + "\n"
                        + "Cookie: " + cookie
        );

        body = "key=value";
        HttpBody httpBody = new HttpBody(body);

        httpRequest = new HttpRequest(startLine, headers, httpBody);
    }

    @DisplayName("BufferedReader를 사용하여 HttpRequest를 생성한다.")
    @Test
    void httpRequestFromBufferedReader() throws Exception {
        // given
        String contentType = "application/x-www-form-urlencoded";
        String cookie = "sessionId=abc123";
        String body = "key=value";
        int contentLength = body.getBytes().length;

        String rawRequest =
                "GET /path HTTP/1.1\n" +
                        "Content-Type: " + contentType + "\n"
                        + "Content-Length: " + contentLength + "\n"
                        + "Cookie: " + cookie + "\n"
                        + "\n" +
                        body;
        BufferedReader reader = new BufferedReader(new StringReader(rawRequest));

        // when
        HttpRequest result = HttpRequest.from(reader);

        // then
        assertAll(
                () -> assertThat(result.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(result.getUri().toString()).hasToString("/path"),
                () -> assertThat(result.getContentType()).isEqualTo(ContentType.FORM_DATA),
                () -> assertThat(result.getCookies().toString()).hasToString(cookie),
                () -> assertThat(result.getBody()).isEqualTo(body)
        );
    }

    @DisplayName("getKeyValueBodies() 메서드는 본문을 파싱하여 키-값 쌍을 반환한다.")
    @Test
    void getKeyValueBodies() {
        // when
        Map<String, String> keyValueBodies = httpRequest.getKeyValueBodies();

        // then
        assertAll(
                () -> assertThat(keyValueBodies).containsEntry("key", "value")
        );
    }

    @DisplayName("getContentType() 메서드는 헤더에서 Content-Type을 반환한다.")
    @Test
    void getContentType() {
        // when
        ContentType contentType = httpRequest.getContentType();

        // then
        assertThat(contentType).isEqualTo(ContentType.FORM_DATA);
    }

    @DisplayName("getBody() 메서드는 요청 본문을 반환한다.")
    @Test
    void getBody() {
        // when
        String actualBody = httpRequest.getBody();

        // then
        assertThat(actualBody).isEqualTo(body);
    }

    @DisplayName("getUri() 메서드는 요청 URI를 반환한다.")
    @Test
    void testGetUri() {
        // when
        URI uri = httpRequest.getUri();

        // then
        assertThat(uri.toString()).hasToString("/path");
    }

    @DisplayName("hasPath() 메서드는 경로가 요청 URI와 일치하는지 확인한다.")
    @Test
    void hasPath() {
        // when
        boolean hasPath = httpRequest.hasPath("/path");

        // then
        assertThat(hasPath).isTrue();
    }

    @DisplayName("getCookies() 메서드는 요청 헤더에서 쿠키를 반환한다.")
    @Test
    void getCookies() {
        // when
        HttpCookies cookies = httpRequest.getCookies();

        // then
        assertThat(cookies.toString()).hasToString(cookie);
    }

    @DisplayName("getMethod() 메서드는 요청 메서드를 반환한다.")
    @Test
    void getMethod() {
        // when
        HttpMethod method = httpRequest.getMethod();

        // then
        assertThat(method).isEqualTo(HttpMethod.GET);
    }
}
