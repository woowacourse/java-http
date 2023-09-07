package org.apache.coyote.view;

import static org.apache.coyote.FixtureFactory.DEFAULT_HEADERS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.FixtureFactory;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.HttpStatus;
import org.apache.coyote.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseResolverTest {


    @Test
    @DisplayName("url의 값을 파싱하여 Response를 생성할 수 있다.")
    void init_test() throws URISyntaxException, IOException {
        Request request = FixtureFactory.getGetRequest("/index.html", DEFAULT_HEADERS);

        Path path = Path.of(ResponseResolver.class.getResource("/static/index.html").toURI());
        byte[] expectedBody = (new String(Files.readAllBytes(path))).getBytes();
        byte[] expectedLine = "HTTP/1.1 200 OK".getBytes();

        Response actual = new ResponseResolver().resolve(request,
                ViewResource.of(request.getPath(), HttpStatus.OK, HttpCookie.from("")));

        assertAll(
                () -> assertThat(actual.getResponseBytes()).startsWith(expectedLine),
                () -> assertThat(actual.getResponseBytes()).contains(expectedBody)
        );
    }

    @Test
    @DisplayName("url의 값이 존재하지 않는다면 404.html을 바디로 가지고 HttpStatusCode가 404인 Response를 생성한다.")
    void no_url() throws URISyntaxException, IOException {
        Request request = FixtureFactory.getGetRequest("/아무것도_없어요.html", DEFAULT_HEADERS);

        Path path = Path.of(ResponseResolver.class.getResource("/static/404.html").toURI());
        byte[] expectedBody = (new String(Files.readAllBytes(path))).getBytes();
        byte[] expectedLine = "HTTP/1.1 404 NOT_FOUND".getBytes();

        Response actual = new ResponseResolver().resolve(request,
                ViewResource.of(request.getPath(), HttpStatus.OK, HttpCookie.from("")));

        assertAll(
                () -> assertThat(actual.getResponseBytes()).startsWith(expectedLine),
                () -> assertThat(actual.getResponseBytes()).contains(expectedBody)
        );
    }

    @Test
    @DisplayName("url의 값이 존재하지않지만 html 문서는 존재할 때 해당 html 문서를 바디로 가지는 Response를 생성한다.")
    void no_url_html() throws IOException, URISyntaxException {
        Request request = FixtureFactory.getGetRequest("/index", DEFAULT_HEADERS);

        Path path = Path.of(ResponseResolver.class.getResource("/static/index.html").toURI());
        byte[] expectedBody = (new String(Files.readAllBytes(path))).getBytes();
        byte[] expectedLine = "HTTP/1.1 200 OK".getBytes();

        Response actual = new ResponseResolver().resolve(request,
                ViewResource.of(request.getPath(), HttpStatus.OK, HttpCookie.from("")));

        assertAll(
                () -> assertThat(actual.getResponseBytes()).startsWith(expectedLine),
                () -> assertThat(actual.getResponseBytes()).contains(expectedBody)
        );
    }
}
