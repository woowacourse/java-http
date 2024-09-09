package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class Http11MethodParserTest {

    public static Stream<Arguments> parseMethodParameters() {
        return Arrays.stream(new String[]{"OPTIONS", "GET", "HEAD", "POST", "PUT", "DELETE", "TRACE", "CONNECT"})
                .map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("parseMethodParameters")
    @DisplayName("존재하는 메서드를 잘 파싱하는지 확인")
    void parseMethod(String httpMethod) {
        Http11MethodParser methodParser = new Http11MethodParser(new Http11StartLineParser());
        String requestMessage = """
                %s /index.html HTTP/1.1\r
                Host: localhost:8080\r
                Connection: keep-alive\r
                Accept: */*\r
                \r
                """.formatted(httpMethod);

        Http11Method http11Method = methodParser.parseMethod(requestMessage);

        Assertions.assertThat(http11Method.name()).isEqualTo(httpMethod);
    }

    @Test
    @DisplayName("존재하지 않는 메서드는 GET으로 판단하는지 확인")
    void parseMethod() {
        Http11MethodParser methodParser = new Http11MethodParser(new Http11StartLineParser());
        String requestMessage = """
                %s /index.html HTTP/1.1\r
                Host: localhost:8080\r
                Connection: keep-alive\r
                Accept: */*\r
                \r
                """.formatted("InvalidMethod");

        Http11Method http11Method = methodParser.parseMethod(requestMessage);

        Assertions.assertThat(http11Method).isEqualTo(Http11Method.GET);
    }
}
