package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Optional;
import org.apache.catalina.session.Session;
import org.junit.jupiter.api.Test;

class HttpResponseHeadersTest {

    @Test
    void addContentTypeTest() {
        HttpResponseHeaders headers = new HttpResponseHeaders();

        headers.addContentType("text/html");

        Optional<String> location = headers.getHeader("Content-Type");
        assertThat(location).contains("text/html");
    }

    @Test
    void addContentLength() {
        HttpResponseHeaders headers = new HttpResponseHeaders();

        headers.addContentLength(100);

        Optional<String> contentLength = headers.getHeader("Content-Length");
        assertThat(contentLength).contains("100");
    }

    @Test
    void addLocationTest() {
        HttpResponseHeaders headers = new HttpResponseHeaders();

        headers.addLocation("/login");

        Optional<String> location = headers.getHeader("Location");
        assertThat(location).contains("/login");
    }

    @Test
    void setSessionTest() {
        HttpResponseHeaders headers = new HttpResponseHeaders();
        Session session = new Session("session-id");

        headers.setSession(session);

        Optional<String> cookie = headers.getHeader("Set-Cookie");
        assertThat(cookie).contains("JSESSIONID=session-id");
    }

    @Test
    void getHeaderTest_whenHeaderExist() {
        HttpResponseHeaders headers = new HttpResponseHeaders(Map.of("key", "value"));

        Optional<String> actual = headers.getHeader("key");

        assertThat(actual).contains("value");
    }

    @Test
    void getHeaderTest_whenHeaderNotExist() {
        HttpResponseHeaders headers = new HttpResponseHeaders(Map.of("key", "value"));

        Optional<String> actual = headers.getHeader("not-exist");

        assertThat(actual).isEmpty();
    }


}
