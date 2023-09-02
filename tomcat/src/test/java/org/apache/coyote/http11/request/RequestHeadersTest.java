package org.apache.coyote.http11.request;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class RequestHeadersTest {

    @Test
    void testRequestHeadersParsing() throws IOException {
        // given
        String requestHeader = "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Accept: */*\r\n" +
                "User-Agent: curl/7.64.1\r\n" +
                "Content-Length: 0\r\n" +
                "\r\n";
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(requestHeader.getBytes())));

        // when
        RequestHeaders requestHeaders = RequestHeaders.parse(bufferedReader);

        // then
        assertSoftly(softly -> {
            softly.assertThat("localhost:8080").isEqualTo(requestHeaders.getHeader("Host"));
            softly.assertThat("keep-alive").isEqualTo(requestHeaders.getHeader("Connection"));
            softly.assertThat("*/*").isEqualTo(requestHeaders.getHeader("Accept"));
            softly.assertThat("curl/7.64.1").isEqualTo(requestHeaders.getHeader("User-Agent"));
            softly.assertThat("0").isEqualTo(requestHeaders.getHeader("Content-Length"));
        });
    }
}
