package org.apache.coyote.http11.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class RequestHeadersTest {

    @Test
    @DisplayName("HttpHeader 파싱 성공 테스트")
    void from() {
        // given
        final String httpRequest = String.join("Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        final BufferedReader br = new BufferedReader(inputStreamReader);

        // when & then
        assertDoesNotThrow(() -> RequestHeaders.from(br));
    }

    @Test
    @DisplayName("파싱한 Header 조회 테스트")
    void getValues() {
        // given
        final String httpRequest = String.join("ocean: king \r\n",
                "wooteco: god \r\n",
                "",
                "");

        final InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        final BufferedReader br = new BufferedReader(inputStreamReader);

        //when
        final RequestHeaders requestHeaders = RequestHeaders.from(br);

        // when & then
        assertAll(
                () -> assertThat(requestHeaders.getValue("ocean")).isEqualTo("king "),
                () -> assertThat(requestHeaders.getValue("wooteco")).isEqualTo("god ")
        );
    }
}
