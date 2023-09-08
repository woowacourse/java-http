package nextstep.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.util.Map;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpHeaderName;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestURI;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class LoginInterceptorTest {

    @DisplayName("경로가 /login 이면 true 를 리턴한다")
    @Test
    void supportReturnTrueLoginPath() {
        // given
        final LoginInterceptor loginInterceptor = new LoginInterceptor();

        // when
        final Map<String, String> requestBody = Map.of(
            "account", "gugu",
            "password", "password"
        );
        final HttpRequest loginHttpRequest = new HttpRequest(new HttpHeaders(), HttpMethod.GET,
            HttpRequestURI.from("/login"), "HTTP/1.1", requestBody);

        // then
        assertThat(loginInterceptor.support(loginHttpRequest)).isTrue();
    }

    @DisplayName("경로가 /login 이 아니면 false 를 리턴한다")
    @Test
    void supportReturnFalseNotLoginPath() {
        // given
        final LoginInterceptor loginInterceptor = new LoginInterceptor();

        // when
        final Map<String, String> requestBody = Map.of(
            "account", "gugu",
            "password", "password"
        );
        final HttpRequest loginHttpRequest = new HttpRequest(new HttpHeaders(), HttpMethod.GET,
            HttpRequestURI.from("/log"), "HTTP/1.1", requestBody);

        // then
        assertThat(loginInterceptor.support(loginHttpRequest)).isFalse();
    }

    @DisplayName("쿠키에 유효한 세션이 존재하면 false 를 리턴한다")
    @Test
    void preHandleIfCookieContained() {
        // given
        final LoginInterceptor loginInterceptor = new LoginInterceptor();

        final Map<String, String> requestBody = Map.of(
            "account", "gugu",
            "password", "password"
        );
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.addHeader(HttpHeaderName.COOKIE, "JSESSIONID=1234");

        final HttpRequest loginHttpRequest = new HttpRequest(httpHeaders, HttpMethod.GET,
            HttpRequestURI.from("/login"), "HTTP/1.1", requestBody);

        final HttpResponse basicResponse = HttpResponse.createBasicResponse();

        final SessionManager sessionManager = mock(SessionManager.class);
        doNothing().when(sessionManager).validateSession(any());

        try (final MockedStatic<SessionManager> mockSessionManger = mockStatic(SessionManager.class)) {
            mockSessionManger.when(SessionManager::getInstance)
                .thenReturn(sessionManager);

            // when
            final boolean result = loginInterceptor.preHandle(loginHttpRequest, basicResponse);

            // then
            assertAll(
                () -> assertThat(result).isFalse(),
                () -> assertThat(basicResponse.getHeaders().getHeaderValue(HttpHeaderName.LOCATION))
                    .contains("/index.html")
            );
        }
    }

    @DisplayName("쿠키가 존재하지 않으면 true 를 리턴한다.")
    @Test
    void preHandleIfCookieNotContained() {
        // given
        final LoginInterceptor loginInterceptor = new LoginInterceptor();

        // when
        final Map<String, String> requestBody = Map.of(
            "account", "gugu",
            "password", "password"
        );
        final HttpRequest loginHttpRequest = new HttpRequest(new HttpHeaders(), HttpMethod.GET,
            HttpRequestURI.from("/login"), "HTTP/1.1", requestBody);

        // then
        assertThat(loginInterceptor.preHandle(loginHttpRequest, HttpResponse.createBasicResponse())).isTrue();
    }
}
