package nextstep.jwp.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import org.apache.catalina.servlet.request.HttpRequest;
import org.apache.catalina.servlet.request.RequestHeaders;
import org.apache.catalina.servlet.request.RequestLine;
import org.apache.catalina.servlet.response.HttpResponse;
import org.apache.catalina.servlet.response.HttpStatus;
import org.apache.catalina.servlet.response.StatusLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("StaticResourceRequestHandler 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class StaticResourceRequestHandlerTest {

    private final StaticResourceRequestHandler handler = new StaticResourceRequestHandler();


    @Test
    void 요청에서_원하는_정적_파일을들_읽어_응답한다() throws IOException {
        // given
        HttpRequest request = HttpRequest.builder()
                .requestLine(RequestLine.from("GET /index.html HTTP/1.1 "))
                .headers(RequestHeaders.from(
                        List.of("Host: localhost:8080 ", "Connection: keep-alive "))
                )
                .build();
        HttpResponse response = new HttpResponse();

        // when
        handler.handle(request, response);

        // then
        URL resource = getClass().getClassLoader().getResource("static/index.html");
        HttpResponse expected = new HttpResponse();
        expected.setStatusLine(new StatusLine(HttpStatus.OK));
        expected.addHeader("Content-Type", "text/html;charset=utf-8");
        expected.setMessageBody(new String(Files.readAllBytes(new File(resource.getFile()).toPath())));
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(expected);
    }


    @Test
    void css_정적_파일을_읽어_응답한다() throws IOException {
        // given
        HttpRequest request = HttpRequest.builder()
                .requestLine(RequestLine.from("GET /css/styles.css HTTP/1.1 "))
                .headers(RequestHeaders.from(
                        List.of("Host: localhost:8080 ", "Connection: keep-alive "))
                )
                .build();
        HttpResponse response = new HttpResponse();

        // when
        handler.handle(request, response);

        // then
        URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        HttpResponse expected = new HttpResponse();
        expected.setStatusLine(new StatusLine(HttpStatus.OK));
        expected.addHeader("Content-Type", "text/css;charset=utf-8");
        expected.setMessageBody(new String(Files.readAllBytes(new File(resource.getFile()).toPath())));
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void js_정적_파일을_읽어_응답한다() throws IOException {
        // given
        HttpRequest request = HttpRequest.builder()
                .requestLine(RequestLine.from("GET /js/scripts.js HTTP/1.1 "))
                .headers(RequestHeaders.from(
                        List.of("Host: localhost:8080 ", "Connection: keep-alive "))
                )
                .build();
        HttpResponse response = new HttpResponse();

        // when
        handler.handle(request, response);

        // then
        URL resource = getClass().getClassLoader().getResource("static/js/scripts.js");
        HttpResponse expected = new HttpResponse();
        expected.setStatusLine(new StatusLine(HttpStatus.OK));
        expected.addHeader("Content-Type", "text/javascript;charset=utf-8");
        expected.setMessageBody(new String(Files.readAllBytes(new File(resource.getFile()).toPath())));
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void 확장자가_없는_경우_html_파일을_반환한다() throws IOException {
        // given
        HttpRequest request = HttpRequest.builder()
                .requestLine(RequestLine.from("GET /login HTTP/1.1 "))
                .headers(RequestHeaders.from(
                        List.of("Host: localhost:8080 ", "Connection: keep-alive "))
                )
                .build();
        HttpResponse response = new HttpResponse();

        // when
        handler.handle(request, response);

        // then
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        HttpResponse expected = new HttpResponse();
        expected.setStatusLine(new StatusLine(HttpStatus.OK));
        expected.addHeader("Content-Type", "text/html;charset=utf-8");
        expected.setMessageBody(new String(Files.readAllBytes(new File(resource.getFile()).toPath())));
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
