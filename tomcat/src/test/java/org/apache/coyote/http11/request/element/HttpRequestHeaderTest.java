package org.apache.coyote.http11.request.element;

import static org.apache.coyote.Constants.CRLF;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.element.HttpMethod;
import org.junit.jupiter.api.Test;

class HttpRequestHeaderTest {

    @Test
    void find() {
        String request = String.join(CRLF, "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive");
        HttpRequestHeader header = HttpRequestHeader.of(request);
        assertThat(header.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(header.getPath()).isEqualTo(Path.of("/index.html"));
        assertThat(header.getQuery()).isEqualTo(new Query(new LinkedHashMap<>()));

        assertThat(header.find("Host")).isEqualTo("localhost:8080");
        assertThat(header.find("Connection")).isEqualTo("keep-alive");
    }

    @Test
    void hasValidSession_true() {
        Session session = new Session(String.valueOf(UUID.randomUUID()));
        SessionManager.get().add(session);

        String requestText = String.join(CRLF, "GET /index.html HTTP/1.1",
                "Cookie: JSESSIONID=" + session.getId(),
                "Host: localhost:8080",
                "Connection: keep-alive");

        HttpRequestHeader requestHeader = HttpRequestHeader.of(requestText);
        HttpRequest request = new HttpRequest(requestHeader, HttpRequestBody.empty());
        assertThat(request.hasValidSession()).isTrue();
    }

    @Test
    void hasValidSession_false() {
        Session session = new Session(String.valueOf(UUID.randomUUID()));

        String requestText = String.join(CRLF, "GET /index.html HTTP/1.1",
                "Cookie: JSESSIONID=" + session.getId(),
                "Host: localhost:8080",
                "Connection: keep-alive");

        HttpRequestHeader requestHeader = HttpRequestHeader.of(requestText);
        HttpRequest request = new HttpRequest(requestHeader, HttpRequestBody.empty());
        assertThat(request.hasValidSession()).isFalse();
    }
}
