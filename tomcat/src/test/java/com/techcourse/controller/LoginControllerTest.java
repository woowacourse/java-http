package com.techcourse.controller;

import com.techcourse.model.User;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("로그인 컨트롤러")
class LoginControllerTest {

    private final Controller staticResourceController = mock(Controller.class);
    private final Manager sessionManager = mock(Manager.class);
    private final LoginController controller = new LoginController(staticResourceController, sessionManager);

    @Nested
    @DisplayName("로그인 페이지 요청")
    class LoginPageRequest {

        @Test
        @DisplayName("로그인되지 않은 사용자는 로그인 페이지 응답을 받는다")
        void returnsLoginPageForAnonymousUser() throws Exception {
            // given
            final var request = request("GET", "", null);
            final var expected = HttpResponse.redirect("/login.html");
            when(staticResourceController.service(request)).thenReturn(expected);

            // when
            final var response = controller.service(request);

            // then
            assertThat(response).isSameAs(expected);
        }

        @Test
        @DisplayName("로그인된 사용자는 인덱스 페이지로 이동시킨다")
        void redirectsLoggedInUserToIndex() throws Exception {
            // given
            final var request = request("GET", "", "Cookie: JSESSIONID=session-id");
            final var session = mock(Session.class);
            when(sessionManager.findSession("session-id")).thenReturn(session);
            when(session.getAttribute("user"))
                    .thenReturn(new User("gugu", "password", "gugu@example.com"));

            // when
            final var response = controller.service(request);

            // then
            assertThat(response.header("Location")).contains("/index.html");
        }
    }

    @Nested
    @DisplayName("로그인 시도")
    class LoginAttempt {

        @Test
        @DisplayName("로그인에 실패하면 인증 실패 페이지로 이동시킨다")
        void redirectsFailedLoginToUnauthorizedPage() throws Exception {
            // given
            final var request = request("POST", "account=gugu&password=wrong", null);

            // when
            final var response = controller.service(request);

            // then
            assertThat(response.header("Location")).contains("/401.html");
        }

        @Test
        @DisplayName("세션 없이 로그인에 성공하면 새 세션 쿠키를 응답한다")
        void issuesSessionCookieAfterSuccessfulLoginWithoutSession() throws Exception {
            // given
            final var request = request("POST", "account=gugu&password=password", null);
            final var session = mock(Session.class);
            when(sessionManager.createSession()).thenReturn(session);
            when(session.getId()).thenReturn("new-session-id");

            // when
            final var response = controller.service(request);

            // then
            assertThat(response.header("Set-Cookie")).contains("JSESSIONID=new-session-id");
        }

        @Test
        @DisplayName("로그인에 성공하면 세션에 사용자를 저장한다")
        void storesLoginUserInSession() throws Exception {
            // given
            final var request = request("POST", "account=gugu&password=password", null);
            final var session = mock(Session.class);
            when(sessionManager.createSession()).thenReturn(session);

            // when
            controller.service(request);

            // then
            verify(session).setAttribute(eq("user"), any(User.class));
        }

        @Test
        @DisplayName("기존 세션으로 로그인하면 새 세션을 생성하지 않는다")
        void reusesExistingSession() throws Exception {
            // given
            final var request = request(
                    "POST",
                    "account=gugu&password=password",
                    "Cookie: JSESSIONID=existing-session");
            final var session = mock(Session.class);
            when(sessionManager.findSession("existing-session")).thenReturn(session);

            // when
            controller.service(request);

            // then
            verify(sessionManager, never()).createSession();
        }

        @Test
        @DisplayName("기존 세션으로 로그인하면 새 세션 쿠키를 응답하지 않는다")
        void doesNotIssueSessionCookieForExistingSession() throws Exception {
            // given
            final var request = request(
                    "POST",
                    "account=gugu&password=password",
                    "Cookie: JSESSIONID=existing-session");
            final var session = mock(Session.class);
            when(sessionManager.findSession("existing-session")).thenReturn(session);

            // when
            final var response = controller.service(request);

            // then
            assertThat(response.header("Set-Cookie")).isEmpty();
        }
    }

    private HttpRequest request(
            final String method,
            final String body,
            final String cookieHeader
    ) throws Exception {
        final var cookie = cookieHeader == null ? "" : cookieHeader + "\r\n";
        final var rawRequest = method + " /login HTTP/1.1\r\n"
                + "Host: localhost:8080\r\n"
                + cookie
                + "Content-Length: " + body.length() + "\r\n"
                + "\r\n"
                + body;
        return HttpRequest.readFrom(new BufferedReader(new StringReader(rawRequest))).orElseThrow();
    }
}
