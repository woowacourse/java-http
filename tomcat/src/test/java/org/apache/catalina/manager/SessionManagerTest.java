package org.apache.catalina.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;

import org.apache.catalina.Session;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {
    @Test
    @DisplayName("Session 조회 기능 테스트 - 세션이 존재할 때")
    void findSessionExists() throws IOException {
        // given
        SessionManager sessionManager = SessionManager.getInstance();
        Session session = Session.createRandomSession();
        sessionManager.add(session);

        HttpRequest request = mock(HttpRequest.class);
        HttpCookie cookie = mock(HttpCookie.class);
        when(cookie.hasJSessionId()).thenReturn(true);
        when(cookie.getJsessionid()).thenReturn(session.getId());
        when(request.getCookie()).thenReturn("JSESSIONID=" + session.getId());

        // When
        Optional<Session> result = sessionManager.findSession(request);

        // Then
        assertAll(
                () -> assertThat(result.isPresent()).isTrue(),
                () -> assertThat(result.get()).isEqualTo(session)
        );
    }

    @Test
    @DisplayName("세션을 조회했을 때 존재하지 않는다.")
    void findSessionNotExists() throws IOException {
        // given
        SessionManager sessionManager = SessionManager.getInstance();
        HttpRequest request = mock(HttpRequest.class);
        HttpCookie cookie = mock(HttpCookie.class);
        when(cookie.hasJSessionId()).thenReturn(true);
        when(request.getCookie()).thenReturn("JSESSIONID=nonExistingSessionId");
        when(cookie.getJsessionid()).thenReturn("nonExistingSessionId");

        // when
        Optional<Session> result = sessionManager.findSession(request);

        // then
        assertThat(result.isPresent()).isFalse();
    }
}
