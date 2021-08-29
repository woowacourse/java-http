package nextstep.jwp.http.message.request;

import nextstep.jwp.fixture.Fixture;
import nextstep.jwp.http.message.element.Headers;
import nextstep.jwp.http.message.element.session.HttpSession;
import nextstep.jwp.http.message.element.session.HttpSessions;
import nextstep.jwp.http.message.element.cookie.Cookie;
import nextstep.jwp.http.message.element.session.Session;
import nextstep.jwp.http.message.request.request_line.HttpMethod;
import nextstep.jwp.http.message.request.request_line.HttpPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    private HttpRequest httpRequest;

    @BeforeEach
    void setUp() {
        this.httpRequest = Fixture.getHttpRequest("/test");
    }

    @DisplayName("특정 httpHeader를 가져온다.")
    @Test
    void getHeader() {
        String host = httpRequest.getHeader("Host")
            .orElseThrow(IllegalArgumentException::new);

        String expected = Fixture.getFixtureHeaders().get("Host");

        assertThat(host).isEqualTo(expected);
    }

    @Test
    void getHttpMethod() {
        HttpMethod httpMethod = httpRequest.getHttpMethod();
        assertThat(httpMethod).isEqualTo(HttpMethod.GET);
    }

    @Test
    void getPath() {
        HttpPath resourcePath = httpRequest.getPath();
        assertThat(resourcePath.getUri()).isEqualTo("/test");
    }

    @DisplayName("언제나 같은 쿠키를 반환한다.")
    @Test
    void getCookie() {
        assertThat(httpRequest.getCookie()).isNotNull();

        Cookie cookie1 = httpRequest.getCookie();
        Cookie cookie2 = httpRequest.getCookie();

        assertThat(cookie1).isSameAs(cookie2);
    }

    @DisplayName("세션이 존재하지 않더라도 언제나 같은 세션을 반환한다.")
    @Test
    void getSession_noValue() {
        assertThat(httpRequest.getSession()).isNotNull();

        Session session1 = httpRequest.getSession();
        Session session2 = httpRequest.getSession();
        assertThat(session1).isSameAs(session2);
    }

    @DisplayName("세션이 존재하면 언제나 같은 세션을 반환한다.")
    @Test
    void getSession_value() {
        Headers headers = new Headers();
        headers.putHeader("Cookie", "JSESSIONID=test");

        HttpRequest httpRequest = Fixture.getHttpRequest("/test", headers);

        HttpSession test = new HttpSession("test");
        HttpSessions.put(test);

        assertThat(httpRequest.getSession()).isNotNull();

        Session session1 = httpRequest.getSession();
        Session session2 = httpRequest.getSession();
        assertThat(session1).isSameAs(session2);
    }
}
