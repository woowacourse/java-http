package nextstep.jwp.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.HttpSessions;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void getCookie() {
        final HttpCookie httpCookie = new HttpCookie("test=test-value");
        final HttpHeaders httpHeaders = new HttpHeaders(new HashMap<>(), httpCookie);
        final HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, "test", "HTTP/1.1", httpHeaders);

        assertThat(httpRequest.getCookie().getCookies("test")).isEqualTo(httpCookie.getCookies("test"));
    }

    @Test
    void getSession() {
        final HttpCookie httpCookie = new HttpCookie("JSESSIONID=1234");
        final HttpHeaders httpHeaders = new HttpHeaders(new HashMap<>(), httpCookie);
        final HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, "test", "HTTP/1.1", httpHeaders);
        final HttpSession httpSession = new HttpSession("1234");

        assertThat(httpRequest.getSession()).isEqualTo(httpSession);
    }
}