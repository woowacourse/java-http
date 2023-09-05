package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {

    private RequestHandler requestHandler = new RequestHandler();

    @Test
    void responseHelloWorld() throws IOException {
        //given
        final var request = new HttpRequest(
                HttpMethod.GET,
                new HttpUri("/"),
                Map.of(),
                Map.of(),
                "",
                Map.of()
        );

        //when
        final var response = requestHandler.handle(request);

        //then
        final var expected = HttpResponse.builder()
                .setHttpStatus(HttpStatus.OK)
                .setContentType(new ContentType("text/plain"))
                .setBody("Hello world!")
                .build();
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/index.html", "/login.html", "/register.html"})
    void responseIndex(String uri) throws IOException {
        //given
        final var request = new HttpRequest(
                HttpMethod.GET,
                new HttpUri(uri),
                Map.of(),
                Map.of(),
                "",
                Map.of()
        );

        //when
        final var response = requestHandler.handle(request);

        //then
        final var resource = getClass().getClassLoader().getResource("static" + uri);
        final var body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        final var expected = HttpResponse.builder()
                .setHttpStatus(HttpStatus.OK)
                .setContentType(new ContentType("text/html"))
                .setBody(body)
                .build();
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(expected);
    }

}
