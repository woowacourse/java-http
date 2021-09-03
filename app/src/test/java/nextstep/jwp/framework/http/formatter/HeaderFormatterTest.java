package nextstep.jwp.framework.http.formatter;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import nextstep.jwp.framework.http.HttpHeaders;

import static org.assertj.core.api.Assertions.assertThat;

class HeaderFormatterTest {

    @DisplayName("HttpHeader 를 HTTP 형식의 String 으로 변환 테스트")
    @ParameterizedTest
    @MethodSource
    void transformTest(HttpHeaders httpHeaders, String expected) {

        // given
        final HeaderFormatter headerFormatter = new HeaderFormatter(httpHeaders);

        // when
        final String httpStatusLine = headerFormatter.transform();

        // then
        assertThat(httpStatusLine).isEqualTo(expected);
    }

    private static Stream<Arguments> transformTest() {
        return Stream.of(
                Arguments.of(
                        new HttpHeaders().addHeader(HttpHeaders.HOST, "localhost:8080"), "Host: localhost:8080 \r\n\r\n"
                ),
                Arguments.of(
                        new HttpHeaders().addHeader(HttpHeaders.HOST, "localhost:8080")
                                         .addHeader(HttpHeaders.CONNECTION, "keep-alive"),

                        "Host: localhost:8080 \r\nConnection: keep-alive \r\n\r\n"
                )
        );
    }
}
