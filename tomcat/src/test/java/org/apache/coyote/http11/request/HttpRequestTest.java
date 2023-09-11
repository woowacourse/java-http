package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.Session;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void 리퀘스트_파싱_테스트() throws IOException {
        String request = "GET /index.html HTTP/1.1\n"
                + "Host: localhost:8080\n"
                + "Connection: keep-alive\n"
                + "Accept: */*\n"
                + "\n"
                + "id=1&password=1234";

        try (var br = new BufferedReader(new StringReader(request))) {
            HttpRequest httpRequest = new HttpRequest(br);
            assertAll(
                    () -> assertThat(httpRequest.getAccept()).isEqualTo("*/*"),
                    () -> assertThat(httpRequest.getMethod()).isEqualTo("GET"),
                    () -> assertThat(httpRequest.getRequestUrl()).isEqualTo("/index.html")
            );
        }
    }

    @Test
    void 세션이_없을_때_리퀘스트_파싱_테스트() throws IOException {
        String request = "GET /index.html HTTP/1.1\n"
                + "Host: localhost:8080\n"
                + "Connection: keep-alive\n"
                + "Accept: */*\n"
                + "Cookie: yummy=1234\n"
                + "\n"
                + "id=1&password=1234";

        try (var br = new BufferedReader(new StringReader(request))) {
            assertThatCode(() -> new HttpRequest(br)).doesNotThrowAnyException();
        }
    }

    @Test
    void 세션_초기화_테스트() {
        String request = "GET /index.html HTTP/1.1\n"
                + "Host: localhost:8080\n"
                + "Connection: keep-alive\n"
                + "Accept: */*\n"
                + "Cookie: JSESSIONID=1234\n"
                + "\n"
                + "id=1&password=1234";

        SessionManager.getInstance().add(new Session("1234"));

        try (var br = new BufferedReader(new StringReader(request))) {
            HttpRequest httpRequest = new HttpRequest(br);
            assertThat(httpRequest.getSession().getId()).isEqualTo("1234");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
