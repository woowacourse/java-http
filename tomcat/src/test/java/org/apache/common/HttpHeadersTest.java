package org.apache.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.apache.request.HttpHeaders;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpHeadersTest {

    @Test
    void HTTP_요청에서_Header_객체를_생성한다() {
        List<String> lines = List.of(
                "Host: localhost:8080 ",
                "Accept: text/html ",
                "Connection: keep-alive "
        );

        HttpHeaders httpHeaders = HttpHeaders.of(lines);

        assertAll(
                () -> assertThat(httpHeaders.getHeaderValue("Host")).isEqualTo("localhost:8080 "),
                () -> assertThat(httpHeaders.getHeaderValue("Accept")).isEqualTo("text/html "),
                () -> assertThat(httpHeaders.getHeaderValue("Connection")).isEqualTo("keep-alive ")
        );
    }
}
