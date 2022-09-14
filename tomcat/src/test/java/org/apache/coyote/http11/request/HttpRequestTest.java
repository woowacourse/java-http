package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import org.apache.coyote.http11.session.Cookie;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void isStaticFileRequest() {
        String protocol = "HTTP/1.1";
        URI uri = new URI("/login.html");
        RequestLine requestLine = new RequestLine(Method.GET, uri, protocol);

        Map<String, String> headers = Map.of("key", "value");
        RequestHeaders requestHeaders = new RequestHeaders(headers);

        RequestBody requestBody = RequestBody.of("id=lala&password=1234");

        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, requestBody);

        assertThat(httpRequest.isStaticFileRequest()).isTrue();
    }

    @Test
    void getExtension() {
        String protocol = "HTTP/1.1";
        URI uri = new URI("/login.html");
        RequestLine requestLine = new RequestLine(Method.GET, uri, protocol);

        Map<String, String> headers = Map.of("key", "value");
        RequestHeaders requestHeaders = new RequestHeaders(headers);

        RequestBody requestBody = RequestBody.of("id=lala&password=1234");

        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, requestBody);

        assertAll(
                () -> assertThat(httpRequest.getExtension()).isPresent(),
                () -> assertThat(httpRequest.getExtension().get()).isEqualTo("html")
        );
    }

    @Test
    void getSession() {
        String protocol = "HTTP/1.1";
        URI uri = new URI("/login.html");
        RequestLine requestLine = new RequestLine(Method.GET, uri, protocol);

        String sessionKey = "12344123";
        Map<String, String> headers = Map.of("Cookie", Cookie.JSESSIONID + "=" + sessionKey);
        RequestHeaders requestHeaders = new RequestHeaders(headers);

        RequestBody requestBody = RequestBody.of("id=lala&password=1234");

        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, requestBody);

        assertAll(
                () -> assertThat(httpRequest.getSession()).isPresent(),
                () -> assertThat(httpRequest.getSession().get()).isEqualTo(sessionKey)
        );
    }
}
