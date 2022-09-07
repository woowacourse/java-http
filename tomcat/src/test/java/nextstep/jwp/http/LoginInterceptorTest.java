package nextstep.jwp.http;

import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.http.HttpHeader;
import org.apache.http.HttpMethod;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LoginInterceptorTest {

    private final LoginInterceptor loginInterceptor = new LoginInterceptor(List.of("/login"));

    @Test
    void includeUri에_포함되지_않으면_true를_반환한다() {
        // given
        final Request request = GET_요청(new Headers(), "/index");

        // when
        final boolean actual = loginInterceptor.preHandle(request, new MockOutputStream());

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void includeUri에_포함되고_Session이_존재하지_않으면_true를_반환한다() {
        // given
        final Request request = GET_요청(new Headers(), "/login");

        // when
        final boolean actual = loginInterceptor.preHandle(request, new MockOutputStream());

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void includeUri에_포함되고_Session이_존재하면_false를_반환하고_index로_리다이렉트한다() {
        // given
        final String sessionId = "randomValue";
        final SessionManager sessionManager = SessionManager.get();
        sessionManager.add(new Session(sessionId));
        final Request request = GET_요청(쿠키에_세션아이디가_존재하는_헤더(sessionId), "/login");
        final OutputStream outputStream = new MockOutputStream();

        // when
        final boolean actual = loginInterceptor.preHandle(request, outputStream);
        // then
        assertAll(
                () -> assertThat(actual).isFalse(),
                () -> assertThat(outputStream.toString()).contains("Location: /index.html")
        );
    }

    private Request GET_요청(final Headers headers, final String uri) {
        final RequestInfo requestInfo = new RequestInfo(HttpMethod.GET, uri);
        return new Request(requestInfo, headers, null);
    }

    private Headers 쿠키에_세션아이디가_존재하는_헤더(final String sessionId) {
        final Headers headers = new Headers();
        headers.put(HttpHeader.COOKIE, "JSESSIONID=" + sessionId);
        return headers;
    }
}
