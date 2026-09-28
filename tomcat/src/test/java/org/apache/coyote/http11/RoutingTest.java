package org.apache.coyote.http11;

import com.techcourse.Application;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RoutingTest {

    @Test
    void 로그인_URL에_쿼리_문자열을_추가해_요청해도_로그인_페이지를_응답한다() throws IOException {
        // given
        String path = "/login?next=index";

        // when
        String response = request("GET", path);

        // then
        assertThat(response).startsWith("HTTP/1.1 200 OK").endsWith(resource("/login.html"));
    }

    @Test
    void CSS_URL에_버전_쿼리를_추가해_요청해도_해당_CSS_파일을_응답한다() throws IOException {
        // given
        String path = "/css/styles.css?v=1";

        // when
        String response = request("GET", path);

        // then
        assertThat(response).startsWith("HTTP/1.1 200 OK").endsWith(resource("/css/styles.css"));
    }

    @Test
    void 루트_URL을_GET으로_요청하면_메인_페이지를_응답한다() throws IOException {
        // given
        String path = "/";

        // when
        String response = request("GET", path);

        // then
        assertThat(response).startsWith("HTTP/1.1 200 OK").endsWith(resource("/index.html"));
    }

    @Test
    void 등록되지_않은_메서드와_경로_조합으로_요청하면_404를_응답한다() {
        // given
        List<String> methods = List.of("POST", "PATCH", "PUT", "DELETE", "CUSTOM");
        for (String method : methods) {
            // when
            String response = request(method, "/index.html");

            // then
            assertThat(response).as(method).startsWith("HTTP/1.1 404 Not Found");
        }
    }

    @Test
    void 존재하지_않는_경로를_GET으로_요청하면_404를_응답한다() {
        // given
        String path = "/missing.js";

        // when
        String response = request("GET", path);

        // then
        assertThat(response).startsWith("HTTP/1.1 404 Not Found");
    }

    @Test
    void 인코딩한_상위_디렉터리_경로를_요청해도_404를_응답한다() {
        // given
        String path = "/%2e%2e/outside.html";

        // when
        String response = request("GET", path);

        // then
        assertThat(response).startsWith("HTTP/1.1 404 Not Found");
    }

    @Test
    void 메서드에_허용되지_않는_문자가_있으면_400을_응답한다() {
        // given
        String method = "GE(T";

        // when
        String response = request(method, "/login");

        // then
        assertThat(response).startsWith("HTTP/1.1 400 Bad Request");
    }

    @Test
    void Content_Length가_음수인_요청에는_400을_응답한다() {
        // given
        StubSocket socket = new StubSocket("POST /login HTTP/1.1\r\nContent-Length: -1\r\n\r\n");

        // when
        new Http11Processor(socket, Application.createRequestMapping()).process(socket);

        // then
        assertThat(socket.output()).startsWith("HTTP/1.1 400 Bad Request");
    }

    private String request(String method, String path) {
        StubSocket socket = new StubSocket(method + " " + path + " HTTP/1.1\r\n\r\n");
        new Http11Processor(socket, Application.createRequestMapping()).process(socket);
        return socket.output();
    }

    private String resource(String path) throws IOException {
        try (InputStream input = getClass().getResourceAsStream("/static" + path)) {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
