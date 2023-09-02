package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HttpRequestTest {

    static Stream<Arguments> arguments() {
        return Stream.of(
                Arguments.of("/index.html", "text/html;charset=utf-8"),
                Arguments.of("/style.css", "text/css;charset=utf-8"),
                Arguments.of("/scripts.js", "application/javascript;charset=utf-8")
        );
    }

    @ParameterizedTest
    @MethodSource("arguments")
    @DisplayName("RequestUri에 해당하는 ContentType이 반환된다.")
    void getContentType(final String requestUri, final String actualContentType) throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET " + requestUri + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(inputStreamReader);
        HttpRequest expectedRequest = HttpRequest.from(br);

        // when
        String expectedContentType = expectedRequest.getContentType();

        // then
        assertThat(expectedContentType).isEqualTo(actualContentType);
    }
}
