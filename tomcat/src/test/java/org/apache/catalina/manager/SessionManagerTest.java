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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {
    private SessionManager sessionManager;
    private Session session;

    @BeforeEach
    void init(){
        sessionManager = SessionManager.getInstance();
        session = Session.createRandomSession();
        sessionManager.add(session);
    }

    @AfterEach
    void remove(){
        sessionManager.remove(session);
    }

    @DisplayName("새로운 세션을 생성하고 저장한다.")
    @Test
    void addSession() throws IOException {
        // given
        HttpRequest request = mock(HttpRequest.class);
        when(request.getCookie()).thenReturn(new HttpCookie("JSESSIONID=" + session.getId()));

        // when
        sessionManager.add(session);

        // then
        Session result = sessionManager.findSession(request).get();

        assertThat(result.getId()).isEqualTo(session.getId());
    }

    @DisplayName("존재하는 세션을 조회해온다.")
    @Test
    void findSessionExists() throws IOException {
        // given
        HttpRequest request = mock(HttpRequest.class);
        HttpCookie cookie = mock(HttpCookie.class);
        when(cookie.hasJSessionId()).thenReturn(true);
        when(cookie.getJsessionid()).thenReturn(session.getId());
        when(request.getCookie()).thenReturn(new HttpCookie("JSESSIONID=" + session.getId()));

        // When
        Optional<Session> result = sessionManager.findSession(request);

        // Then
        assertAll(
                () -> assertThat(result.isPresent()).isTrue(),
                () -> assertThat(result.get()).isEqualTo(session)
        );
    }

    @DisplayName("세션 속성으로 존재하는 세션을 조회해온다.")
    @Test
    void getByAttribute() throws IOException {
        // given
        session.setAttribute("user", "account");
        HttpRequest request = mock(HttpRequest.class);
        HttpCookie cookie = mock(HttpCookie.class);
        when(cookie.hasJSessionId()).thenReturn(true);
        when(cookie.getJsessionid()).thenReturn(session.getId());
        when(request.getCookie()).thenReturn(new HttpCookie("JSESSIONID=" + session.getId()));

        // When
        Optional<Session> result = sessionManager.getByAttribute("user", "account");

        // Then
        assertThat(result.get()).isEqualTo(session);
    }

    @DisplayName("주어진 속성을 가진 세션이 없다.")
    @Test
    void cannotGetByAttribute() throws IOException {
        // given
        session.setAttribute("user", "account");
        HttpRequest request = mock(HttpRequest.class);
        HttpCookie cookie = mock(HttpCookie.class);
        when(cookie.hasJSessionId()).thenReturn(true);
        when(cookie.getJsessionid()).thenReturn(session.getId());
        when(request.getCookie()).thenReturn(new HttpCookie("JSESSIONID=" + session.getId()));

        // When
        Optional<Session> result = sessionManager.getByAttribute("user", "wrongAccount");

        // Then
        assertThat(result).isEmpty();
    }

    @DisplayName("세션을 조회했을 때 존재하지 않는다.")
    @Test
    void findSessionNotExists() throws IOException {
        // given
        HttpRequest request = mock(HttpRequest.class);
        HttpCookie cookie = mock(HttpCookie.class);
        when(cookie.hasJSessionId()).thenReturn(true);
        when(request.getCookie()).thenReturn(new HttpCookie("JSESSIONID=nonExistingSessionId"));
        when(cookie.getJsessionid()).thenReturn("nonExistingSessionId");

        // when
        Optional<Session> result = sessionManager.findSession(request);

        // then
        assertThat(result.isPresent()).isFalse();
    }

    @DisplayName("세션을 삭제한다.")
    @Test
    void removeSession() throws IOException {
        // given
        HttpRequest request = mock(HttpRequest.class);
        when(request.getCookie()).thenReturn(new HttpCookie("JSESSIONID=" + session.getId()));

        // when
        sessionManager.remove(session);

        // then
        Optional<Session> result = sessionManager.findSession(request);

        assertThat(result.isPresent()).isFalse();
    }
}
