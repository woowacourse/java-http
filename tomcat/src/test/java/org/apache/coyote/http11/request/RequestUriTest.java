package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class RequestUriTest {

    static Stream<Arguments> testDataForRequestUri() {
        return Stream.of(
                Arguments.of("/index", "/index.html"),
                Arguments.of("/index.html", "/index.html"),
                Arguments.of("/", "/")
        );
    }

    static Stream<Arguments> testDataForGetRequestUrl() {
        return Stream.of(
                Arguments.of("/index", "/index"),
                Arguments.of("/index.html", "/index"),
                Arguments.of("/index.index.html", "/index.index")
        );
    }

    static Stream<Arguments> testDataForGetExtension() {
        return Stream.of(
                Arguments.of("/index", "html"),
                Arguments.of("/index.html", "html"),
                Arguments.of("/styles.css", "css"),
                Arguments.of("/", "html")
        );
    }

    @DisplayName("접근 가능한 형태의 RequestUri를 만들 수 있다.")
    @MethodSource("testDataForRequestUri")
    @ParameterizedTest
    void testRequestUri(final String rawRequestUri, final String expected) {
        // given & when
        final RequestUri requestUri = new RequestUri(rawRequestUri);

        // then
        final String actual = requestUri.getRequestUri();

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("RequestUrl을 가져올 수 있다.")
    @MethodSource("testDataForGetRequestUrl")
    @ParameterizedTest
    void testGetRequestUrl(final String rawRequestUri, final String expected) {
        // given
        final RequestUri requestUri = new RequestUri(rawRequestUri);

        // when
        final String actual = requestUri.getRequestUrl();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("Extension을 가져올 수 있다.")
    @MethodSource("testDataForGetExtension")
    @ParameterizedTest
    void testGetExtension(final String rawRequestUri, final String expected) {
        // given
        final RequestUri requestUri = new RequestUri(rawRequestUri);

        // when
        final String actual = requestUri.getExtension();

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
