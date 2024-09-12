package org.apache.catalina.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;

import org.apache.catalina.auth.HttpCookie;
import org.apache.catalina.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpRequestTest {
    @Nested
    @DisplayName("생성")
    class Constructor {
        @Test
        @DisplayName("성공 : Request 규정에 따라 적절하게 주어진 경우 생성 성공")
        void ConstructorSuccess() {
            RequestLine requestLine = new RequestLine("GET /index?account=gugu&password=password HTTP/1.1");
            Map<String, String> headers = Map.of("Content-Length", "text/html", "Cookie", "id=gugu");
            RequestHeader requestHeader = new RequestHeader(headers);
            Map<String, String> body = Map.of("body", "hello");
            RequestBody requestBody = new RequestBody(body);

            HttpRequest httpRequest = new HttpRequest(requestLine, requestHeader, requestBody);

            assertAll(() -> assertThat(httpRequest.getQueryParam()).isEqualTo(
                            Map.of("account", "gugu", "password", "password")),
                    () -> assertThat(httpRequest.getPathWithoutQuery()).isEqualTo("/index"),
                    () -> assertThat(httpRequest.getContentType()).isEqualTo(ContentType.HTML),
                    () -> assertThat(httpRequest.getCookie()).isEqualTo(new HttpCookie(Map.of("id", "gugu"))),
                    () -> assertThat(httpRequest.getBody()).isEqualTo(body));
        }
    }
}
