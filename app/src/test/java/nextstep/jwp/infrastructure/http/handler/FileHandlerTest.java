package nextstep.jwp.infrastructure.http.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.infrastructure.http.FileResolver;
import nextstep.jwp.infrastructure.http.Headers;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.request.Method;
import nextstep.jwp.infrastructure.http.request.RequestLine;
import nextstep.jwp.infrastructure.http.response.HttpResponse;
import nextstep.jwp.infrastructure.http.response.ResponseLine;
import nextstep.jwp.infrastructure.http.response.StatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FileHandlerTest {

    @DisplayName("특정 파일을 읽어 응답으로 매핑한다.")
    @Test
    void handle() throws Exception {
        final String url = "/hello.html";
        final URL resource = getClass().getClassLoader().getResource("static/" + url);
        final Headers expectedHeaders = new Headers();
        expectedHeaders.add("Content-Type", "text/html;charset=utf-8");
        expectedHeaders.add("Content-Length", "12");

        final HttpResponse expected = new HttpResponse(
            new ResponseLine(StatusCode.OK),
            expectedHeaders,
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );

        assertResponse(url, expected);
    }

    @DisplayName("존재하지 않는 파일인 경우 404.html이 매핑된다.")
    @Test
    void notFound() throws Exception {
        final String url = "/notfounddddd.html";
        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        final Headers expectedHeaders = new Headers();
        expectedHeaders.add("Content-Type", "text/html;charset=utf-8");
        expectedHeaders.add("Content-Length", "2426");

        final HttpResponse expected = new HttpResponse(
            new ResponseLine(StatusCode.NOT_FOUND),
            expectedHeaders,
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );

        assertResponse(url, expected);
    }

    void assertResponse(final String url, final HttpResponse expected) throws Exception {
        final HttpRequest request = new HttpRequest(new RequestLine(Method.GET, url), new Headers(), "");
        final HttpResponse response = new HttpResponse();
        final FileHandler fileHandler = new FileHandler(new FileResolver("static"));

        fileHandler.handle(request, response);
        assertThat(response).isEqualTo(expected);
    }
}