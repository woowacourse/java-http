package nextstep.jwp.http.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("request 에서 쿠키 가져오기 - 성공")
    @Test
    void getCookie() {
        final HttpCookie httpCookie = new HttpCookie("test=test-value");
        final HttpHeaders httpHeaders = new HttpHeaders(new HashMap<>(), httpCookie);
        final HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, "test", "HTTP/1.1",
                httpHeaders);

        assertThat(httpRequest.getCookie().getCookies("test"))
                .isEqualTo(httpCookie.getCookies("test"));
    }

    @DisplayName("request 에서 세션 가져오기 - 성공")
    @Test
    void getSession() {
        final HttpCookie httpCookie = new HttpCookie("JSESSIONID=1234");
        final HttpHeaders httpHeaders = new HttpHeaders(new HashMap<>(), httpCookie);
        final HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, "test", "HTTP/1.1",
                httpHeaders);
        final HttpSession httpSession = new HttpSession("1234");

        assertThat(httpRequest.getSession().getId()).isEqualTo(httpSession.getId());
    }
}