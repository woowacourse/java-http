package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.FixtureFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestTest {

    private static final String sessionId = UUID.randomUUID().toString(); // 현재 어플리케이션 내 세션 값과 다르게 사용

    SessionManager sessionManager = SessionManager.getInstance();
    Session session = new Session(sessionId);

    @BeforeEach
    void init() {
        sessionManager.add(session);
    }

    @AfterEach
    void finish() {
        sessionManager.remove(session);
    }

    @Test
    @DisplayName("Cookie에 담긴 JSESSION 정보를 사용해 세션을 얻을 수 있다.")
    void get_session_true_o() {
        Map<String, String> header = new HashMap<>() {{
            put("Accept", "*/*");
            put("Host", "localhost:8080");
            put("Connection", "keep-alive");
            put("Cookie", "JSESSIONID=" + sessionId);
        }};
        Request request = FixtureFactory.getGetRequest("/login", header);
        Session session = request.getSession(true);

        Assertions.assertThat(session.getId()).isEqualTo(sessionId);
    }

    @Test
    @DisplayName("getSession에 true를 전달하면 JSESSION의 값이 없더라도 새로운 세션을 얻을 수 있다.")
    void get_session_true_x() {
        Map<String, String> header = new HashMap<>() {{
            put("Accept", "*/*");
            put("Host", "localhost:8080");
            put("Connection", "keep-alive");
        }};
        Request request = FixtureFactory.getGetRequest("/login", header);
        Session session = request.getSession(true);

        Assertions.assertThat(session).isInstanceOf(Session.class);
    }

    @Test
    @DisplayName("Cookie에 담긴 JSESSION 정보를 사용해 세션을 얻을 수 있다.")
    void get_session_false_o() {
        Map<String, String> header = new HashMap<>() {{
            put("Accept", "*/*");
            put("Host", "localhost:8080");
            put("Connection", "keep-alive");
            put("Cookie", "JSESSIONID=" + sessionId);
        }};
        Request request = FixtureFactory.getGetRequest("/login", header);
        Session session = request.getSession(false);

        Assertions.assertThat(session.getId()).isEqualTo(sessionId);
    }

    @Test
    @DisplayName("getSession에 false 전달하면 JSESSION의 값이 없을 때 새로운 세션을 얻을 수 없다.")
    void get_session_false_x() {
        Map<String, String> header = new HashMap<>() {{
            put("Accept", "*/*");
            put("Host", "localhost:8080");
            put("Connection", "keep-alive");
        }};
        Request request = FixtureFactory.getGetRequest("/login", header);
        Session session = request.getSession(false);

        Assertions.assertThat(session).isNull();
    }

    @Test
    @DisplayName("세션 관리자에 저장되지 않은 JSESSIONID로 세션을 찾으면 예외가 발생한다. - true 전달")
    void get_session_true_throw() {
        Map<String, String> header = new HashMap<>() {{
            put("Accept", "*/*");
            put("Host", "localhost:8080");
            put("Connection", "keep-alive");
            put("Cookie", "JSESSIONID=" + UUID.randomUUID());
        }};
        Request request = FixtureFactory.getGetRequest("/login", header);

        assertThatThrownBy(() -> request.getSession(true))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("세션 관리자에 저장되지 않은 JSESSIONID로 세션을 찾으면 null이 전달된다. - false 전달")
    void get_session_false_throw() {
        Map<String, String> header = new HashMap<>() {{
            put("Accept", "*/*");
            put("Host", "localhost:8080");
            put("Connection", "keep-alive");
            put("Cookie", "JSESSIONID=" + UUID.randomUUID());
        }};
        Request request = FixtureFactory.getGetRequest("/login", header);

        assertThatThrownBy(() -> request.getSession(false))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
