package org.apache.catalina.servlets.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import org.apache.catalina.controller.http.request.HttpMethod;
import org.apache.catalina.controller.http.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    private final String request = String.join("\r\n",
            "GET /login/hello?account=account&password=password HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            "Accept: */*",
            "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
            "Custom: hello: hi",
            "",
            "nickname=hi&role=member");

    @Test
    @DisplayName("요청 메시지에서 method를 파싱해서 저장한다.")
    void parseHttpMethod() {
        HttpRequest httpRequest = new HttpRequest(request);

        assertThat(httpRequest.getMethod()).isEqualTo(HttpMethod.GET);
    }

    @Test
    @DisplayName("요청 메시지에서 path를 파싱해서 저장한다.")
    void parsePath() {
        HttpRequest httpRequest = new HttpRequest(request);

        assertThat(httpRequest.getPath()).isEqualTo("/login/hello?account=account&password=password");
    }

    @Test
    @DisplayName("요청 메시지에서 프로토콜 버전을 파싱해서 저장한다.")
    void parseVersion() {
        HttpRequest httpRequest = new HttpRequest(request);

        assertThat(httpRequest.getProtocolVersion()).isEqualTo("HTTP/1.1");
    }

    @Test
    @DisplayName("헤더를 파싱해서 map으로 저장한다.")
    void parseHeaders() {
        HttpRequest httpRequest = new HttpRequest(request);

        Map<String, String> headers = httpRequest.getHeaders();

        assertThat(headers).isNotEmpty()
                .isEqualTo(Map.of(
                        "Host", "localhost:8080",
                        "Connection", "keep-alive",
                        "Accept", "*/*",
                        "Cookie",
                        "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
                        "Custom", "hello: hi"
                ));
    }

    @Test
    @DisplayName("요청 바디를 파싱해서 값을 저장한다.")
    void parseBody() {
        HttpRequest httpRequest = new HttpRequest(request);

        assertThat(httpRequest.getBody()).isEqualTo("nickname=hi&role=member");
    }

    @Test
    @DisplayName("url의 쿼리 파라미터를 Map 형태로 반환한다.")
    void getQueryString() {
        HttpRequest httpRequest = new HttpRequest(request);

        assertThat(httpRequest.getQueryParameters())
                .containsOnly(
                        entry("account", new String[]{"account"}),
                        entry("password", new String[]{"password"}));
    }

    @Test
    @DisplayName("요청의 uri를 반환한다.")
    void getRequestURI() {
        HttpRequest httpRequest = new HttpRequest(request);

        assertThat(httpRequest.getRequestURI())
                .isEqualTo("/login/hello");
    }

    @Test
    @DisplayName("요청의 url을 반환한다.")
    void getRequestURL() {
        HttpRequest httpRequest = new HttpRequest(request);

        assertThat(httpRequest.getRequestURL())
                .isEqualTo("http://localhost:8080/login/hello");
    }

    @Test
    @DisplayName("모든 파라미터를 가져온다.")
    void getParameters() {
        String request = String.join("\r\n",
                "GET /login/hello?account=account&password=password HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Accept: */*",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
                "Custom: hello: hi",
                "",
                "nickname=hi,hello,bye&role=member");

        HttpRequest httpRequest = new HttpRequest(request);

        assertThat(httpRequest.getParameters())
                .containsOnly(
                        entry("account", new String[]{"account"}),
                        entry("password", new String[]{"password"}),
                        entry("nickname", new String[]{"hi", "hello", "bye"}),
                        entry("role", new String[]{"member"}));
    }

    @Test
    @DisplayName("파라미터 이름을 전달하면 값을 반환한다.")
    void getParameter() {
        HttpRequest httpRequest = new HttpRequest(request);

        assertAll(
                () -> assertThat(httpRequest.getParameter("account")).isEqualTo("account"),
                () -> assertThat(httpRequest.getParameter("password")).isEqualTo("password")
        );
    }
}
