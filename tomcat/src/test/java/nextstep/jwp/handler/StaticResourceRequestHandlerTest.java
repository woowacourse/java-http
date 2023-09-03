package nextstep.jwp.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import org.apache.catalina.servlet.request.HttpRequest;
import org.apache.catalina.servlet.request.RequestHeaders;
import org.apache.catalina.servlet.request.StartLine;
import org.apache.catalina.servlet.response.HttpResponse;
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
                .startLine(StartLine.from("GET /index.html HTTP/1.1 "))
                .headers(RequestHeaders.from(
                        List.of("Host: localhost:8080 ", "Connection: keep-alive "))
                )
                .build();

        // when
        HttpResponse response = handler.handle(request);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(response.toString()).isEqualTo(expected);
    }

    @Test
    void 확장자가_없는_경우_html_파일을_반환한다() throws IOException {
        // given
        HttpRequest request = HttpRequest.builder()
                .startLine(StartLine.from("GET /login HTTP/1.1 "))
                .headers(RequestHeaders.from(
                        List.of("Host: localhost:8080 ", "Connection: keep-alive "))
                )
                .build();

        // when
        HttpResponse response = handler.handle(request);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 3447 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(response.toString()).isEqualTo(expected);
    }
}
