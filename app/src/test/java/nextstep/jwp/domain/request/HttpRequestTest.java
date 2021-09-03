package nextstep.jwp.domain.request;

import nextstep.jwp.MockSocket;
import nextstep.jwp.domain.Converter;
import nextstep.jwp.domain.HttpCookie;
import nextstep.jwp.domain.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    private static final String JSESSIONID = "656cef62-e3c4-40bc-a8df-94732920ed46";
    private MockSocket socket;
    private HttpRequest httpRequest;

    @BeforeEach
    void setUp() throws IOException {
        String request = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080 ",
                "Accept: */* ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                "");

        socket = new MockSocket(request);
        InputStream inputStream = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        httpRequest = Converter.convertToHttpRequest(reader);
    }

    @DisplayName("Http Request Method를 반환한다.")
    @Test
    void getMethod() {
        //given
        //when
        String method = httpRequest.getMethod();
        //then
        assertThat(method).isEqualTo("GET");
    }

    @DisplayName("Http Request Line의 URI를 반환한다.")
    @Test
    void getUri() {
        //given
        //when
        String uri = httpRequest.getUri();
        //then
        assertThat(uri).isEqualTo("/index.html");
    }

    @DisplayName("Http Request의 QueryMap을 반환한다.")
    @Test
    void getQueryMap() throws IOException {
        //given
        String request = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1",
                "Host: localhost:8080 ",
                "Accept: */*",
                "",
                "");
        socket = new MockSocket(request);
        InputStream inputStream = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        //when
        HttpRequest httpRequest = Converter.convertToHttpRequest(reader);
        //then
        assertThat(httpRequest.getQueryMap().size()).isEqualTo(2);
    }

    @DisplayName("Request Header Map을 반환한다.")
    @Test
    void headers() {
        //given
        //when
        Map<String, String> httpHeaders = httpRequest.getHttpHeaders();
        //then
        assertThat(httpHeaders.size()).isEqualTo(3);
    }

    @DisplayName("Request Header를 반환한다.")
    @Test
    void getHttpHeader() {
        //given
        //when
        //then
        assertThat(httpRequest.getHttpHeader("Host")).isEqualTo("localhost:8080");
    }

    @DisplayName("Session을 반환한다.")
    @Test
    void getSession() {
        //given
        //when
        HttpSession session = httpRequest.getSession();
        //then
        assertThat(session.getId()).isEqualTo(JSESSIONID);
    }

    @DisplayName("HttpCookie를 반환한다.")
    @Test
    void getHttpCookie() {
        //given
        //when
        HttpCookie httpCookie = httpRequest.getHttpCookie();
        //then
        assertThat(httpCookie.getSessionId()).isEqualTo(JSESSIONID);
    }
}