package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class RequestHeaderTest {
    private static final String FAKE_SESSION_ID = "abcdefghijklmnopqrstuvwxyz";

    static Stream<Arguments> testDataForRequestHeader() {
        return Stream.of(
                Arguments.of("Host: localhost:8080\r\nConnection: keep-alive", Map.of("Host", "localhost:8080", "Connection", "keep-alive")),
                Arguments.of("Host: localhost:8080", Map.of("Host", "localhost:8080")),
                Arguments.of("Host: localhost:8080\r\nConnection:", Map.of("Host", "localhost:8080")),
                Arguments.of("Host: localhost:8080\rConnection: keep-alive", Map.of()),
                Arguments.of("Host", Map.of()),
                Arguments.of("", Map.of())
        );
    }

    static Stream<Arguments> testDataForGetContentLength() {
        return Stream.of(
                Arguments.of("Content-Length: 12", 12),
                Arguments.of("Host: localhost:8080", 0)
        );
    }

    static Stream<Arguments> testDataForGetJSessionId() {
        return Stream.of(
                Arguments.of("Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=" + FAKE_SESSION_ID, FAKE_SESSION_ID),
                Arguments.of("Cookie: yummy_cookie=choco; tasty_cookie=strawberry", ""),
                Arguments.of("Host: localhost:8080", "")
        );
    }

    static Stream<Arguments> testDataForGetMimeType() {
        return Stream.of(
                Arguments.of("Accept: text/css,*/*;q=0.1", "text/css"),
                Arguments.of("Accept: */*", "*/*"),
                Arguments.of("Host: localhost:8080", "")
        );
    }

    @DisplayName("문자열을 파싱해서 헤더를 {key, value} 형태로 저장할 수 있다.")
    @MethodSource("testDataForRequestHeader")
    @ParameterizedTest
    void testRequestHeader(final String rawRequestHeader, final Map<String, String> expected) {
        // given & when
        final RequestHeader requestHeader = new RequestHeader(rawRequestHeader);

        // then
        final Map<String, String> actual = requestHeader.getHeader();

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("Content-Length를 가져올 수 있다.")
    @MethodSource("testDataForGetContentLength")
    @ParameterizedTest
    void testGetContentLength(final String rawRequestHeader, final int expected) {
        // given
        final RequestHeader requestHeader = new RequestHeader(rawRequestHeader);

        // when
        final int actual = requestHeader.getContentLength();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("Content-Length에 문자열이 담겨 있으면, 가져올 수 없다.")
    @Test
    void testThrowErrorIfContentLengthString() {
        // given
        final RequestHeader requestHeader = new RequestHeader("Content-Length: length");

        // when & then
        assertThatThrownBy(requestHeader::getContentLength)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Content-Length는 숫자 형식이어야 합니다.");
    }

    @DisplayName("Cookie에 있는 JSESSIONID를 가져올 수 있다.")
    @MethodSource("testDataForGetJSessionId")
    @ParameterizedTest
    void testGetJSessionId(final String rawRequestHeader, final String expected) {
        // given
        final RequestHeader requestHeader = new RequestHeader(rawRequestHeader);

        // when
        final String actual = requestHeader.getJSessionId();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("Accept에서 MimeType을 가져올 수 있다.")
    @MethodSource("testDataForGetMimeType")
    @ParameterizedTest
    void testGetMimeType(final String rawRequestHeader, final String expected) {
        // given
        final RequestHeader requestHeader = new RequestHeader(rawRequestHeader);

        // when
        final String actual = requestHeader.getMimeType();

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
