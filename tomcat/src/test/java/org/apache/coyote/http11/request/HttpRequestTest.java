package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HTTP 요청 테스트")
class HttpRequestTest {

    @DisplayName("HTTP 요청의 메서드, 경로, 버전을 파싱한다.")
    @Test
    void httpRequestMethodPathVersionParse() throws IOException {
        // given
        String method = "GET";
        String path = "/index.html";
        String version = "HTTP/1.1";

        String httpRequest = String.join("\r\n",
                method + " " + path + " " + version,
                "",
                "");

        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes(StandardCharsets.UTF_8));

        // when
        HttpRequest request = new HttpRequest(inputStream);

        // then
        assertAll(
                () -> assertThat(request.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(request.getPath()).isEqualTo(path),
                () -> assertThat(request.getVersion().getVersionString()).isEqualTo(version)
        );
    }

    @DisplayName("HTTP 요청 헤더 파싱에 성공한다.")
    @Test
    void httpRequestHeaderParse() throws IOException {
        // given
        String contentLengthKey = "Content-Length";
        String contentLengthValue = "123";

        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                contentLengthKey + ": " + contentLengthValue,
                "",
                "");

        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes(StandardCharsets.UTF_8));

        // when
        HttpRequest request = new HttpRequest(inputStream);

        // then
        assertThat(request.getHeaders().getContentLength()).isEqualTo(Integer.parseInt(contentLengthValue));
    }
}
