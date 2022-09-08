package org.apache.coyote.http11.web;

import static org.apache.coyote.http11.support.HttpHeader.COOKIE;
import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.support.HttpHeaders;
import org.apache.coyote.http11.support.HttpStartLine;
import org.apache.coyote.http11.support.session.Session;
import org.apache.coyote.http11.support.session.SessionManager;
import org.apache.coyote.http11.web.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import java.util.Map;

class AuthenticationInterceptorTest {

    @DisplayName("IncludeUri에 포함된 경로와 함께 유효한 Session으로 요청할 경우, false를 반환한다.")
    @Test
    void preHandle_returnsFalse_ifIncludedUriAndValidSession() {
        // given
        final AuthenticationInterceptor authenticationInterceptor = new AuthenticationInterceptor(List.of("/login"));
        final HttpStartLine httpStartLine = HttpStartLine.from(new String[]{"GET", "/login", "HTTP/1.1"});
        final HttpHeaders httpHeaders = new HttpHeaders(Map.of(COOKIE, "JSESSIONID=12345"));
        final HttpRequest httpRequest = new HttpRequest(httpStartLine, httpHeaders, "");
        final SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.add(new Session("12345"));

        // when
        final boolean actual = authenticationInterceptor.preHandle(httpRequest);

        // then
        assertThat(actual).isFalse();
    }

    @DisplayName("IncludeUri에 해당하는 uri가 아닌 경우, true를 반환한다.")
    @Test
    void preHandle_returnsTrue_ifNotIncludedInUri() {
        // given
        final AuthenticationInterceptor authenticationInterceptor =
                new AuthenticationInterceptor(Collections.emptyList());
        final HttpStartLine httpStartLine = HttpStartLine.from(new String[]{"GET", "/something", "HTTP/1.1"});
        final HttpRequest httpRequest = new HttpRequest(httpStartLine, new HttpHeaders(Collections.emptyMap()), "");

        // when
        final boolean actual = authenticationInterceptor.preHandle(httpRequest);

        // then
        assertThat(actual).isTrue();
    }

    @DisplayName("요청에 Cookie가 존재하지 않는 경우 true를 반환한다.")
    @Test
    void preHandle_returnsTrue_ifSessionNotExists() {
        // given
        final AuthenticationInterceptor authenticationInterceptor = new AuthenticationInterceptor(List.of("/login"));
        final HttpStartLine httpStartLine = HttpStartLine.from(new String[]{"GET", "/login", "HTTP/1.1"});
        final HttpRequest httpRequest = new HttpRequest(httpStartLine, new HttpHeaders(Collections.emptyMap()), "");

        // when
        final boolean actual = authenticationInterceptor.preHandle(httpRequest);

        // then
        assertThat(actual).isTrue();
    }

    @DisplayName("요청에 Cookie에 session이 있지만 유효하지 않은 session인 경우 true를 반환한다.")
    @Test
    void preHandle_returnsTrue_ifInvalidSession() {
        // given
        final AuthenticationInterceptor authenticationInterceptor = new AuthenticationInterceptor(List.of("/login"));
        final HttpStartLine httpStartLine = HttpStartLine.from(new String[]{"GET", "/login", "HTTP/1.1"});
        final HttpHeaders httpHeaders = new HttpHeaders(Map.of(COOKIE, "JSESSIONID=12345"));
        final HttpRequest httpRequest = new HttpRequest(httpStartLine, httpHeaders, "");

        // when
        final boolean actual = authenticationInterceptor.preHandle(httpRequest);

        // then
        assertThat(actual).isTrue();
    }
}
