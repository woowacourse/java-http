package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.coyote.http11.constant.HttpMethods;
import org.apache.coyote.http11.cookie.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestTest {

    private static final String rawRequest = String.join("\r\n",
            "GET /index.html?param1=456&param2=789 HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Cookie: JSESSIONID=a58129vbbdaae2rew12",
            "Content-Length: " + "params3=123&params4=678".getBytes().length,
            "",
            "param3=123&param4=678");

    HttpRequest request;

    @BeforeEach
    void setUp() {
        InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        RequestAssembler requestAssembler = new RequestAssembler();
        try {
            request = requestAssembler.makeRequest(bufferedReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Resource인 요청인지 확인한다.")
    void isResource() {

        assertThat(request.isResource()).isTrue();
    }

    @Test
    @DisplayName("요청에 Cookie가 있는 경우 정상적으로 가지고 있는지 확인한다.")
    void hasCookie() {

        assertThat(request.hasCookie()).isTrue();
    }

    @Test
    @DisplayName("요청에 들어온 쿠키를 가지고 있는지 확인한다.")
    void getCookies() {
        Cookie cookies = request.getCookies();

        assertThat(cookies.hasCookie("JSESSIONID")).isTrue();
    }

    @Test
    @DisplayName("body에 데이터가 있는 경우 정상적으로 가지고 있는지 확인한다.")
    void getBody() {
        Map<String, String> bodies = request.getBody();

        assertThat(bodies.get("param3")).isNotNull();
    }

    @Test
    @DisplayName("요청에 대한 Url을 리턴한다.")
    void getUrl() {

        assertThat(request.getUrl()).isEqualTo("/index.html");
    }

    @Test
    @DisplayName("쿼리 스트링이 있는경우 읽어온다.")
    void getQueryString() {
        Map<String, String> queryString = request.getQueryString();

        assertThat(queryString.get("param1")).isNotNull();
    }

    @Test
    @DisplayName("메소드 이름을 읽어온다.")
    void getMethod() {
        HttpMethods actual = request.getMethod();

        assertThat(actual).isEqualTo(HttpMethods.GET);
    }
}