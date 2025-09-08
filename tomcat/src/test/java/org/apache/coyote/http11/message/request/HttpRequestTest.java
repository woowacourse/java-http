package org.apache.coyote.http11.message.request;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void 평문_요청으로_HttpRequest를_생성한다() {
        // given
        String rawRequest = "GET /index.html HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "\r\n";
        BufferedReader reader = new BufferedReader(new StringReader(rawRequest));

        // when & then
        assertThatCode(() -> HttpRequest.from(reader))
                .doesNotThrowAnyException();
    }

    @Test
    void 요청_라인이_유효하지_않으면_예외() {
        // given
        String rawRequest = "GET /index.html\r\n" + // HTTP/1.1 누락
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "\r\n";

        BufferedReader reader = new BufferedReader(new StringReader(rawRequest));

        // when & then
        assertThatThrownBy(() -> HttpRequest.from(reader))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    void 요청_파싱이_정상적으로_되는지_검증() throws IOException {
        String body = "account=gugu&password=password&email=hkkang%40woowahan.com";

        String rawRequest =
                "POST /login HTTP/1.1\r\n" +
                        "Host: localhost:8080\r\n" +
                        "Connection: keep-alive\r\n" +
                        "Content-Length: " + body.length() + "\r\n" +
                        "Content-Type: application/x-www-form-urlencoded\r\n" +
                        "Accept: */*\r\n" +
                        "\r\n" +
                        body;
        BufferedReader reader = new BufferedReader(new StringReader(rawRequest));
        HttpRequest request = HttpRequest.from(reader);

        // 요청 라인 검증
        assertEquals(HttpMethod.POST, request.getMethod());
        assertEquals("/login", request.getRequestPath());
        assertEquals("HTTP/1.1", request.getVersion());

        // 헤더 검증
        assertEquals("localhost:8080", request.getHeaders().getFirst("Host"));
        assertEquals("keep-alive", request.getHeaders().getFirst("Connection"));
        assertEquals(String.valueOf(body.length()), request.getHeaders().getFirst("Content-Length"));
        assertEquals("application/x-www-form-urlencoded", request.getHeaders().getFirst("Content-Type"));

        // 쿼리 파라미터 (없으므로 empty)
        assertTrue(request.getQueryParams().isEmpty());

        // 바디 파라미터 검증
        Map<String, String> bodyParams = request.getBodyParams();
        assertEquals(3, bodyParams.size());
        assertEquals("gugu", bodyParams.get("account"));
        assertEquals("password", bodyParams.get("password"));
        assertEquals("hkkang@woowahan.com", bodyParams.get("email"));
    }
}
