package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static support.HttpRequestFixtures.httpRequest;

class HttpRequestTest {

    @Test
    void Content_Length를_요청_헤더에서_조회한다() {
        String rawRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: 30",
                "Cookie: JSESSIONID=session-id",
                "",
                "account=gugu&password=password"
        );

        HttpRequest request = httpRequest(rawRequest);

        assertThat(request.getContentLength()).isEqualTo(30);
    }

    @Test
    void Content_Length_헤더가_없으면_0을_반환한다() {
        String rawRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "",
                ""
        );

        HttpRequest request = httpRequest(rawRequest);

        assertThat(request.getContentLength()).isZero();
    }

    @Test
    void Content_Length만큼_본문을_바이트_단위로_읽는다() throws IOException {
        String body = "한글본문";
        String rawRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "",
                body
        );
        ByteArrayInputStream inputStream = new ByteArrayInputStream(
                rawRequest.getBytes(StandardCharsets.UTF_8)
        );

        HttpRequest request = HttpRequest.from(inputStream);

        assertThat(request.getBody()).isEqualTo(body);
    }

    @Test
    void 쿼리_문자열이_없으면_빈_파라미터를_반환한다() {
        HttpRequest request = httpRequest("GET /index.html HTTP/1.1\r\n\r\n");

        assertThat(request.getQueryParameters()).isEmpty();
    }

    @Test
    void 요청_본문이_비어_있으면_빈_파라미터를_반환한다() {
        HttpRequest request = httpRequest("GET /index.html HTTP/1.1\r\n\r\n");

        assertThat(request.getBodyParameters()).isEmpty();
    }
}
