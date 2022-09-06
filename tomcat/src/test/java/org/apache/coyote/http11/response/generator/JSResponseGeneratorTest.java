package org.apache.coyote.http11.response.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class JSResponseGeneratorTest {

    private static final JSResponseGenerator JS_RESPONSE_GENERATOR = new JSResponseGenerator();

    @DisplayName("처리할 수 있는 HttpRequest인지 반환한다.")
    @ParameterizedTest
    @MethodSource("provideRequestAndExpected")
    void isSuitable(String request, boolean expected) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        boolean actual = JS_RESPONSE_GENERATOR.isSuitable(HttpRequest.from(bufferedReader));

        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideRequestAndExpected() {
        return Stream.of(
                Arguments.of(
                        "GET /js/scripts.js HTTP/1.1\n" +
                                "Host: localhost:8080\n" +
                                "Connection: keep-alive\n" +
                                "",
                        true),
                Arguments.of(
                        "GET /index.html HTTP/1.1\n" +
                                "Host: localhost:8080\n" +
                                "Connection: keep-alive\n" +
                                "",
                        false)
        );
    }

    @DisplayName("HttpResponse를 반환한다.")
    @Test
    void generate() throws IOException {
        String request = "GET /js/scripts.js HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "Connection: keep-alive\n" +
                "";
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        HttpResponse httpResponse = JS_RESPONSE_GENERATOR.generate(HttpRequest.from(bufferedReader));

        assertThat(httpResponse.getResponse())
                .contains("200 OK")
                .contains("Content-Type: text/js;charset=utf-8")
                .contains("Content-Length: 976");
    }
}
