package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class HttpRequestTest {
    private static final String CHARSET_UTF8 = ";charset=utf-8";

    static Stream<Arguments> testDataForGetMimeType() {
        return Stream.of(
                Arguments.of("GET /index.html HTTP/1.1", "Accept: text/html", "text/html" + CHARSET_UTF8),
                Arguments.of("GET /index.html HTTP/1.1", "", "text/html" + CHARSET_UTF8),
                Arguments.of("GET /index.html HTTP/1.1", "Accept: */*", "text/html" + CHARSET_UTF8)
        );
    }

    @DisplayName("MimeType을 가져올 수 있다.")
    @MethodSource("testDataForGetMimeType")
    @ParameterizedTest
    void testGetMimeType(final String requestLine, final String headers, final String expected) {
        // given
        final HttpRequest httpRequest = new HttpRequest(requestLine, headers);

        // when
        final String actual = httpRequest.getMimeType();

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
