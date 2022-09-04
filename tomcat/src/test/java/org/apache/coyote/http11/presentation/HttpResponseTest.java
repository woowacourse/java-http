package org.apache.coyote.http11.presentation;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.domain.HttpStatus;
import org.apache.coyote.http11.util.ResourceLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

class HttpResponseTest {

    @DisplayName("HTTP 응답 포매팅이 제대로 이루어지는지 확인한다.")
    @Test
    void format() throws IOException {
        // given
        final List<String> lines = List.of("GET /index.html HTTP/1.1", "Host: localhost:8080",
                "Connection: keep-alive", "Accept: */*");
        final HttpRequest httpRequest = HttpRequest.of(lines);
        final HttpResponse httpResponse = new HttpResponse(
                HttpStatus.OK,
                httpRequest.getAcceptType(),
                ResourceLoader.getContent(httpRequest.getUri())
        );

        // when
        final String formattedResponse = httpResponse.format();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: */* \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(formattedResponse).isEqualTo(expected);
    }
}
