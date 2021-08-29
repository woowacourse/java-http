package nextstep.jwp.http.message.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.fixture.Fixture;
import nextstep.jwp.http.message.element.Headers;
import nextstep.jwp.http.message.element.HttpSession;
import nextstep.jwp.http.message.element.HttpSessions;
import nextstep.jwp.http.message.request.request_line.HttpMethod;
import nextstep.jwp.http.message.request.request_line.HttpPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

    @Test
    void getCookie() {
        assertThat(httpRequest.getCookie()).isNotNull();
    }

    @DisplayName("세션이 존재하지 않는다면 Optional null을 반환한다.")
    @Test
    void getSession_noValue() {
        assertThat(httpRequest.getSession()).isNotPresent();
    }

    @DisplayName("세션이 존재하면 Session을 반환한다.")
    @Test
    void getSession_value() {
        Headers headers = new Headers();
        headers.putHeader("Cookie", "JSESSIONID=test");

        HttpRequest httpRequest = Fixture.getHttpRequest("/test", headers);

        HttpSession test = new HttpSession("test");
        HttpSessions.put(test);

        assertThat(httpRequest.getSession()).isPresent();
    }
}
