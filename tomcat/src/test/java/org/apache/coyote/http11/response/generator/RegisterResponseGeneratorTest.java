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

class RegisterResponseGeneratorTest {

    private static final RegisterResponseGenerator REGISTER_RESPONSE_GENERATOR = new RegisterResponseGenerator();

    @DisplayName("처리할 수 있는 HttpRequest인지 반환한다.")
    @ParameterizedTest
    @MethodSource("provideRequestAndExpected")
    void isSuitable(String request, boolean expected) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        boolean actual = REGISTER_RESPONSE_GENERATOR.isSuitable(HttpRequest.from(bufferedReader));

        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideRequestAndExpected() {
        return Stream.of(
                Arguments.of(
                        "POST /register HTTP/1.1\n" +
                                "Host: localhost:8080\n" +
                                "Connection: keep-alive\n" +
                                "Content-Length: 58\n" +
                                "\n" +
                                "account=gugu&password=password&email=hkkang%40woowahan.com",
                        true),
                Arguments.of(
                        "GET /index.html HTTP/1.1\n" +
                                "Host: localhost:8080\n" +
                                "Connection: keep-alive\n",
                        false)
        );
    }

    @DisplayName("HttpResponse를 반환한다.")
    @Test
    void generate() throws IOException {
        String request = "POST /register HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "Connection: keep-alive\n" +
                "Content-Length: 58\n" +
                "\n" +
                "account=gugu&password=password&email=hkkang%40woowahan.com";
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.from(bufferedReader);
        String redirectLocation = "http://localhost:8080/index.html";

        HttpResponse httpResponse = REGISTER_RESPONSE_GENERATOR.generate(httpRequest);

        assertThat(httpResponse.getResponse())
                .contains("302 Found")
                .contains("Location: " + redirectLocation);
    }
}