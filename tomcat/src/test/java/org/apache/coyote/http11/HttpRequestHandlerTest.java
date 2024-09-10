package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestHandlerTest {

    @DisplayName("GET / 를 요청하면 Hello world!를 반환한다.")
    @Test
    void rootUrl() {
        // given
        RequestLine requestLine = new RequestLine("GET / HTTP/1.1");
        RequestHeaders requestHeaders = new RequestHeaders(List.of());
        String responseBody = "Hello world!";
        HttpRequest request = new HttpRequest(requestLine, requestHeaders, "");
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        );

        // when
        HttpRequestHandler handler = new HttpRequestHandler();
        HttpResponse response = handler.handle(request);
        String actual = response.build();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("GET /index.html 를 요청하면 index.html 내용을 반환한다.")
    @Test
    void indexHtml() throws IOException {
        // given
        RequestLine requestLine = new RequestLine("GET /index.html HTTP/1.1");
        RequestHeaders requestHeaders = new RequestHeaders(List.of());
        HttpRequest request = new HttpRequest(requestLine, requestHeaders, "");
        URL resource = getClass().getClassLoader().getResource("static/index.html");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        );

        // when
        HttpRequestHandler handler = new HttpRequestHandler();
        HttpResponse response = handler.handle(request);
        String actual = response.build();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("존재하지 않는 자원을 를 요청하면 404.html 내용을 반환한다.")
    @Test
    void wrongHtml() throws IOException {
        // given
        RequestLine requestLine = new RequestLine("GET /wrong.html HTTP/1.1");
        RequestHeaders requestHeaders = new RequestHeaders(List.of());
        HttpRequest request = new HttpRequest(requestLine, requestHeaders, "");
        URL resource = getClass().getClassLoader().getResource("static/404.html");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        String expected = String.join("\r\n",
                "HTTP/1.1 404 Not Found ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        );

        // when
        HttpRequestHandler handler = new HttpRequestHandler();
        HttpResponse response = handler.handle(request);
        String actual = response.build();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("지원하지 않은 메서드를 요청하면 405 응답을 반환한다.")
    @Test
    void methodNotAllow() throws IOException {
        // given
        RequestLine requestLine = new RequestLine("GET /register HTTP/1.1");
        RequestHeaders requestHeaders = new RequestHeaders(List.of());
        HttpRequest request = new HttpRequest(requestLine, requestHeaders, "");
        String expected = String.join("\r\n",
                "HTTP/1.1 405 Method Not Allowed ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 0 ",
                "\r\n"
        );

        // when
        HttpRequestHandler handler = new HttpRequestHandler();
        HttpResponse response = handler.handle(request);
        String actual = response.build();

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
