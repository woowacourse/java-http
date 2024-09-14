package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.common.Cookie;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.HttpMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class HttpRequestTest {

    private HttpRequest httpRequest;
    private RequestLine requestLine;
    private HttpHeaders headers;
    private RequestBody requestBody;

    @BeforeEach
    void setUp() {
        requestLine = Mockito.mock(RequestLine.class);
        headers = Mockito.mock(HttpHeaders.class);
        requestBody = Mockito.mock(RequestBody.class);
        httpRequest = new HttpRequest(requestLine, headers, requestBody);
    }

    @Test
    @DisplayName("세션 쿠키가 존재하지 않을 경우 세션이 존재하지 않는다고 판단한다")
    void testSessionNotExistsWithoutCookie() {
        Mockito.when(headers.getHeaders()).thenReturn(Map.of());

        boolean result = httpRequest.sessionNotExists();

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("JSESSIONID 쿠키가 없는 경우 세션이 존재하지 않는다고 판단해야 한다")
    void testSessionNotExistsWithoutJSESSIONID() {
        Mockito.when(headers.getHeaders()).thenReturn(Map.of(HttpHeaders.COOKIE, "someOtherCookie=value"));

        boolean result = httpRequest.sessionNotExists();

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("유효한 JSESSIONID가 있는 경우 세션이 존재한다고 판단해야 한다")
    void testSessionExistsWithValidJSESSIONID() {
        Session session = new Session();
        SessionManager.add(session);
        Cookie cookie = Cookie.session(session.getId());

        Mockito.when(headers.getHeaders()).thenReturn(Map.of(HttpHeaders.COOKIE, cookie.getCookieString()));

        boolean result = httpRequest.sessionNotExists();
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("getSession(false)시 세션이 존재하지 않을 경우 null을 반환해야 한다")
    void testGetSessionReturnsNullWhenNotExists() {
        Mockito.when(headers.getHeaders()).thenReturn(Map.of());

        Session session = httpRequest.getSession(false);

        assertThat(session).isNull();
    }

    @Test
    @DisplayName("getSession(true)시 세션이 존재하지 않을 경우 세션을 생성해야 한다")
    void testGetSessionCreatesNewSessionWhenNotExists() {
        Mockito.when(headers.getHeaders()).thenReturn(Map.of());

        Session session = httpRequest.getSession(true);

        assertThat(session).isNotNull();
    }

    @Test
    @DisplayName("HTTP 메소드가 같은지 확인해야 한다")
    void testIsSameMethod() {
        HttpMethod method = HttpMethod.GET;
        Mockito.when(requestLine.getMethod()).thenReturn(method);

        boolean result = httpRequest.isSameMethod(method);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("쿼리 문자열을 반환해야 한다")
    void testGetQueryString() {
        Map<String, String> queryString = Map.of("param1", "value1");
        Mockito.when(requestLine.getQueryString()).thenReturn(queryString);

        Map<String, String> result = httpRequest.getQueryString();

        assertThat(result).isEqualTo(queryString);
    }

    @Test
    @DisplayName("파라미터를 반환해야 한다")
    void testGetParams() {
        Map<String, String> params = Map.of("param1", "value1");
        Mockito.when(requestBody.getParams()).thenReturn(params);

        Map<String, String> result = httpRequest.getParams();

        assertThat(result).isEqualTo(params);
    }
}
