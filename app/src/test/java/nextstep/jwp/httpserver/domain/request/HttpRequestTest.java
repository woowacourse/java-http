package nextstep.jwp.httpserver.domain.request;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.httpserver.domain.Body;
import nextstep.jwp.httpserver.domain.Cookie;
import nextstep.jwp.httpserver.domain.Headers;
import nextstep.jwp.httpserver.domain.HttpVersion;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HttpRequest 도메인 테스트")
class HttpRequestTest {

    @Test
    @DisplayName("Request 요청에 JSESSIONID 쿠키가 있을 때의 값")
    void sessionIdInCookie() {
        // given
        HttpRequest httpRequest = new HttpRequest(
                new RequestLine(HttpMethod.GET, "/index.html", HttpVersion.HTTP_1_1),
                new Headers(),
                Collections.singletonList(new Cookie("JSESSIONID", "ABCDE")),
                null,
                new Body()
        );

        // when
        String sessionId = httpRequest.sessionIdInCookie();

        // then
        assertThat(sessionId).isEqualTo("ABCDE");
    }

    @Test
    @DisplayName("Request 요청에 JSESSIONID 쿠키가 없을 때의 값")
    void sessionIdNotInCookie() {
        // given
        HttpRequest httpRequest = new HttpRequest(
                new RequestLine(HttpMethod.GET, "/index.html", HttpVersion.HTTP_1_1),
                new Headers(),
                new ArrayList<>(),
                null,
                new Body()
        );

        // when
        String sessionId = httpRequest.sessionIdInCookie();

        // then
        assertThat(sessionId).isEqualTo("");
    }

    @Test
    @DisplayName("Request 요청에 JSESSIONID 쿠키가 있는 경우")
    void hasSessionId() {
        // given
        HttpRequest httpRequest = new HttpRequest(
                new RequestLine(HttpMethod.GET, "/index.html", HttpVersion.HTTP_1_1),
                new Headers(),
                Collections.singletonList(new Cookie("JSESSIONID", "ABCDE")),
                null,
                new Body()
        );

        // when
        boolean exist = httpRequest.hasSessionId();

        // then
        assertThat(exist).isTrue();
    }

    @Test
    @DisplayName("Request 요청에 JSESSIONID 쿠키가 없는 경우")
    void noSessionId() {
        // given
        HttpRequest httpRequest = new HttpRequest(
                new RequestLine(HttpMethod.GET, "/index.html", HttpVersion.HTTP_1_1),
                new Headers(),
                new ArrayList<>(),
                null,
                new Body()
        );

        // when
        boolean exist = httpRequest.hasSessionId();

        // then
        assertThat(exist).isFalse();
    }
}
