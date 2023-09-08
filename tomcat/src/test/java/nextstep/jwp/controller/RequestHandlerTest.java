package nextstep.jwp.controller;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpPath;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.ResponseBody;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.apache.coyote.http11.HttpMethod.GET;
import static org.apache.coyote.http11.HttpVersion.HTTP_1_1;
import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {

    private RequestHandler requestHandler = new RequestHandler();

    @Test
    void responseHelloWorld() throws Exception {
        //given
        final var request = new HttpRequest(
                GET,
                new HttpPath("/"),
                HTTP_1_1,
                Map.of(),
                Map.of(),
                HttpRequest.EMPTY,
                Map.of()
        );

        final var response = HttpResponse.prepareFrom(request);

        //when
        requestHandler.service(request, response);

        //then
        final var expected = HttpResponse.builder()
                .httpVersion(HTTP_1_1)
                .httpStatus(HttpStatus.OK)
                .body(ResponseBody.from("Hello world!"))
                .build();

        assertThat(response).usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/index.html", "/login.html", "/register.html"})
    void responseIndex(String uri) throws Exception {
        //given
        final var request = new HttpRequest(
                GET,
                new HttpPath(uri),
                HTTP_1_1,
                Map.of(),
                Map.of(),
                "",
                Map.of()
        );
        final var response = HttpResponse.prepareFrom(request);

        //when
        requestHandler.service(request, response);

        //then
        final var resource = getClass().getClassLoader().getResource("static" + uri);
        final var body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        final var expected = HttpResponse.builder()
                .httpVersion(HTTP_1_1)
                .httpStatus(HttpStatus.OK)
                .body(ResponseBody.of(new ContentType("text/html"), body))
                .build();
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void index() throws Exception {
        //given
        final var request = HttpRequest.builder()
                .method(GET)
                .path(new HttpPath("/index.html"))
                .version(HTTP_1_1)
                .headers(Map.of("Host", "localhost:8080", "Connection", "keep-alive"))
                .build();
        final var response = HttpResponse.prepareFrom(request);

        //when
        requestHandler.service(request, response);

        //then
        final var body = ResponseBody.of(new ContentType("text/html"), buildMessageBody("/index.html"));
        final var expect = HttpResponse.builder()
                .httpVersion(HTTP_1_1)
                .httpStatus(HttpStatus.OK)
                .body(body)
                .build();

        assertThat(response).usingRecursiveComparison()
                .isEqualTo(expect);
    }

    private String buildMessageBody(String staticFileUri) throws Exception {
        final var resource = getClass().getClassLoader().getResource("static" + staticFileUri);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
