package org.apache.catalina;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpRequestHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.Method;
import org.apache.coyote.http11.RequestLine;
import org.apache.coyote.http11.Session;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.HashMap;

class AuthManagerTest {

    @DisplayName("인증된 유저가 아니면 예외를 던진다.")
    @Test
    void not_authenticate() {
        // given
        RequestLine requestLine = new RequestLine(Method.GET, "/login", "HTTP/1.1");
        HashMap<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Cookie", "Idea-56f698fe=c5be6597-c8ed-4450-bd98-59a6db9c0a1d;");
        HttpRequestHeaders httpRequestHeaders = new HttpRequestHeaders(requestHeaders);
        String requestBody = "account=zeze&password=1234";
        HttpRequest httpRequest = new HttpRequest(requestLine, httpRequestHeaders, requestBody);

        // when & then
        Assertions.assertThatCode(() -> AuthManager.authenticate(httpRequest))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("회원가입된 유저가 아닙니다.");
    }

    @DisplayName("유저를 인증 후 인증정보를 전달한다.")
    @Test
    void authenticate() {
        // given
        InMemoryUserRepository.save(new User("zeze", "1234", "zeze@gmail.com"));

        RequestLine requestLine = new RequestLine(Method.GET, "/login", "HTTP/1.1");
        HashMap<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Cookie", "Idea-56f698fe=c5be6597-c8ed-4450-bd98-59a6db9c0a1d;");
        HttpRequestHeaders httpRequestHeaders = new HttpRequestHeaders(requestHeaders);
        String requestBody = "account=zeze&password=1234";
        HttpRequest httpRequest = new HttpRequest(requestLine, httpRequestHeaders, requestBody);

        // when & then
        Assertions.assertThatCode(() -> AuthManager.authenticate(httpRequest))
                .doesNotThrowAnyException();

    }

    @DisplayName("쿠키에 인증 정보가 없으면 isAuthenticated() 메서드에서 false를 반환한다.")
    @Test
    void isAuthenticated_false_whenAuthenticationCookieNotExist() {
        // given
        RequestLine requestLine = new RequestLine(Method.GET, "/login", "HTTP/1.1");
        HashMap<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Cookie", "Idea-56f698fe=c5be6597-c8ed-4450-bd98-59a6db9c0a1d;");
        HttpRequestHeaders httpRequestHeaders = new HttpRequestHeaders(requestHeaders);
        HttpRequest httpRequest = new HttpRequest(requestLine, httpRequestHeaders);

        // when
        boolean authenticated = AuthManager.isAuthenticated(httpRequest);

        // then
        Assertions.assertThat(authenticated).isFalse();
    }

    @DisplayName("인증정보가 올바르지 않을 때 isAuthenticated() 메서드에서 false를 반환한다.")
    @Test
    void isAuthenticated_false_whenIAuthenticationCookieInvalid() {
        // given
        String invalidSessionId = "5cef724d-daf9-4ef1-9f13-5e9103a2aa";

        String sessionId = "0ddf724d-daf9-4ef1-9f13-5e9103a2d0aa";
        Session session = new Session(sessionId);
        session.setAttribute("user", sessionId);
        SessionManager.add(session);

        RequestLine requestLine = new RequestLine(Method.GET, "/login", "HTTP/1.1");
        HashMap<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Cookie", "Idea-56f698fe=c5be6597-c8ed-4450-bd98-59a6db9c0a1d; JSESSIONID=" + invalidSessionId);
        HttpRequestHeaders httpRequestHeaders = new HttpRequestHeaders(requestHeaders);
        HttpRequest httpRequest = new HttpRequest(requestLine, httpRequestHeaders);

        // when
        boolean authenticated = AuthManager.isAuthenticated(httpRequest);

        // then
        Assertions.assertThat(authenticated).isFalse();
    }

    @DisplayName("인증정보가 올바르면 isAuthenticated() 메서드에서 true를 반환한다.")
    @Test
    void isAuthenticated_true() {
        // given
        String sessionId = "0ddf724d-daf9-4ef1-9f13-5e9103a2d0aa";
        Session session = new Session(sessionId);
        session.setAttribute("user", sessionId);
        SessionManager.add(session);

        RequestLine requestLine = new RequestLine(Method.GET, "/login", "HTTP/1.1");
        HashMap<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Cookie", "Idea-56f698fe=c5be6597-c8ed-4450-bd98-59a6db9c0a1d; JSESSIONID=" + sessionId);
        HttpRequestHeaders httpRequestHeaders = new HttpRequestHeaders(requestHeaders);
        HttpRequest httpRequest = new HttpRequest(requestLine, httpRequestHeaders);

        // when
        boolean authenticated = AuthManager.isAuthenticated(httpRequest);

        // then
        Assertions.assertThat(authenticated).isTrue();
    }
}
