package org.apache.coyote.http11.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class RequestBodyTest {

    @Test
    @DisplayName("RequsteBody 생성 성공 테스트")
    void of() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "body check");

        final InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        // when & then
        assertDoesNotThrow(() -> RequestBody.of("10", bufferedReader));
    }

    @Test
    @DisplayName("RequsteBody 조회 성공 테스트")
    void getContent() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "body check");

        final InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        final RequestBody requestBody = RequestBody.of("10", bufferedReader);

        // when & then
        assertAll(
                () -> assertThat(requestBody.getRequestBody()).isEqualTo("body check"),
                () -> assertThat(requestBody.getContentLength()).isEqualTo(10)
        );
    }
}
