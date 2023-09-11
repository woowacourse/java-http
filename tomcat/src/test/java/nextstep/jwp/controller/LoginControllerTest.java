package nextstep.jwp.controller;

import common.http.Request;
import common.http.Response;
import common.http.Session;
import org.apache.catalina.startup.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static common.Constants.CRLF;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LoginControllerTest {

    @Test
    void 세션이_존재하는_경우_indexhtml로_리다이렉션을_한다 () {
        // given
        Request request = mock(HttpRequest.class);
        Response response = new HttpResponse();
        Session session = new Session("id");
        SessionManager sessionManager = mock(SessionManager.class);
        LoginController loginController = new LoginController();

        when(request.getSession()).thenReturn(session);
        when(request.getVersionOfTheProtocol()).thenReturn("HTTP/1.1");
        when(sessionManager.findSession(any())).thenReturn(session);
        when(request.hasValidSession()).thenReturn(true);

        // when
        loginController.doGet(request, response);

        // then
        assertThat(response.toString()).hasToString(
                "HTTP/1.1 302 Found " + CRLF +
                "Set-Cookie: JSESSIONID=id " + CRLF +
                "Location: /index.html " + CRLF
        );
    }

    @Test
    void 세션이_존재하지_않는_경우_loginhtml을_응답에_실어준다() {
        // given
        Request request = mock(HttpRequest.class);
        Response response = new HttpResponse();
        LoginController loginController = new LoginController();

        // when
        loginController.doGet(request, response);

        // then
        assertThat(response.getStaticResourcePath()).isEqualTo("/login.html");
    }
}
