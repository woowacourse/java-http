package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.exception.UncheckedHttpException;
import org.apache.coyote.http11.component.HttpCookie;
import org.apache.coyote.http11.component.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpRequestHeaderTest {

    private String sessionId;
    private Session session;
    private SessionManager sessionManager;

    @BeforeEach
    void setup() {
        sessionId = UUID.randomUUID().toString();
        session = new Session(sessionId);
        sessionManager = SessionManager.getInstance();
    }

    @DisplayName("키, 값 구분자로 헤더를 구분한 개수가 2개가 아닌 경우 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"Manner: maketh : man", "makeTheWorld: ", "qwerty"})
    void createInvalidForm(String header) {
        //given
        List<String> given = List.of(header);

        //when //then
        assertThatThrownBy(() -> HttpRequestHeader.from(given))
                .isInstanceOf(UncheckedHttpException.class)
                .hasMessageContaining("헤더 키와 값 형식이 잘못되었습니다.");
    }

    @DisplayName("헤더와 세션 매니저에 모두 세션이 존재하면 그 세션을 반환한다.")
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void getSession(boolean needSession) {
        //given
        sessionManager.add(session);
        String sessionCookie = HttpCookie.ofJSessionId(sessionId).getCookieToMessage();
        List<String> headers = List.of(HttpHeaders.COOKIE + ": " + sessionCookie);
        HttpRequestHeader requestHeader = HttpRequestHeader.from(headers);

        //when
        Session result = requestHeader.getSession(needSession);

        //then
        assertThat(result).isEqualTo(session);
    }

    @DisplayName("헤더나 세션 매니저에 존재 않을 때 세션이 필요하면 새로운 새션을 반환한다.")
    @Test
    void getSessionWithNeedSession() {
        //given
        String sessionCookie = HttpCookie.ofJSessionId(sessionId).getCookieToMessage();
        List<String> headers = List.of(HttpHeaders.COOKIE + ": " + sessionCookie);
        HttpRequestHeader requestHeader = HttpRequestHeader.from(headers);

        //when
        Session result = requestHeader.getSession(true);

        //then
        assertThat(result).isNotNull().isNotEqualTo(session);
    }

    @DisplayName("헤더나 세션 매니저에 존재 않을 때 세션이 필요하면 널을 반환한다.")
    @Test
    void getSessionWithNoNeedSession() {
        //given
        String sessionCookie = HttpCookie.ofJSessionId(sessionId).getCookieToMessage();
        List<String> headers = List.of(HttpHeaders.COOKIE + ": " + sessionCookie);
        HttpRequestHeader requestHeader = HttpRequestHeader.from(headers);

        //when
        Session result = requestHeader.getSession(false);

        //then
        assertThat(result).isNull();
    }
}
