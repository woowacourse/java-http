package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class RequestHeadersTest {

    @ParameterizedTest
    @CsvSource(value = {"Host:true", "Accept:false"}, delimiter = ':')
    @DisplayName("Header가 포함되어 있는지 검증한다.")
    void containsHeader(final String headerName, final boolean expected) {
        // given
        final HttpMessage message = generateMessage();
        RequestHeaders headers = new RequestHeaders(message);

        // when
        final boolean actual = headers.contains(headerName);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(value = {"Host:localhost:8080", "Content-Length:39"}, delimiter = ':')
    @DisplayName("Header의 값을 검증한다.")
    void containsHeader(final String headerName, final String expected) {
        // given
        final HttpMessage message = generateMessage();
        RequestHeaders headers = new RequestHeaders(message);

        // when
        final String actual = headers.getHeaderValue(headerName);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    private HttpMessage generateMessage() {
        final String httpRequest = String.join("\r\n",
            "GET /path HTTP/1.1",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: 39",
            "Content-Type: application/x-www-form-urlencoded",
            "",
            "name=sojukang&email=kangsburg@gmail.com");
        final InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());

        return new HttpMessage(inputStream);
    }
}
