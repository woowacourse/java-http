package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.apache.coyote.message.HttpHeader;
import org.apache.coyote.message.HttpMethod;
import org.apache.coyote.message.HttpRequest;
import org.apache.coyote.message.HttpStatusCode;
import org.apache.coyote.message.RequestLine;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpRequestRouterTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "a", "/abc", "/a.bc", "/index.html2"
    })
    void notFound(final String uri) throws IOException {
        // given
        final var handler = new HttpRequestRouter();

        final var get_http11 = new RequestLine(HttpMethod.GET, uri, "HTTP/1.1");
        final var empty_header = new HttpHeader(Map.of());
        final var request = new HttpRequest(get_http11, empty_header);

        // when
        final var result = handler.route(request);

        // then
        final var resource = getClass().getClassLoader().getResource("static/404.html");
        final var content = Files.readString(Path.of(resource.getPath()));
        assertAll(
                () -> assertThat(result.getStatusLine().statusCode()).isEqualTo(HttpStatusCode.NOT_FOUND),
                () -> assertThat(result.getBody()).isEqualTo(content)
        );
    }
}
