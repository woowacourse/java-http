package nextstep.jwp.controller;

import common.http.Request;
import common.http.Response;
import common.http.Session;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.startup.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.apache.Constants.CRLF;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LoginControllerTest {

    @Test
    void GET요청시_세션이_존재하는_경우_indexhtml로_리다이렉션을_한다 () {
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
        assertThat(response.getMessage()).hasToString(
                "HTTP/1.1 302 Found " + CRLF +
                "Set-Cookie: JSESSIONID=id " + CRLF +
                "Location: /index.html " + CRLF
        );
    }

    @Test
    void GET요청시_세션이_존재하지_않는_경우_loginhtml을_응답에_실어준다() {
        // given
        Request request = mock(HttpRequest.class);
        Response response = new HttpResponse();
        LoginController loginController = new LoginController();

        // when
        loginController.doGet(request, response);

        // then
        assertThat(response.getStaticResourcePath()).isEqualTo("/login.html");
    }

    @Test
    void POST요청시_회원_정보가_없으면_예외를_반환한다() {
        // given
        Request request = mock(HttpRequest.class);
        Response response = new HttpResponse();
        LoginController loginController = new LoginController();

        when(request.getAccount()).thenReturn("롤스로이스");
        when(request.getVersionOfTheProtocol()).thenReturn("HTTP/1.1");

        // expect
        assertThatThrownBy(() -> loginController.doPost(request, response))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("회원 정보가 존재하지 않습니다.");
    }

    @Test
    void POST요청시_비밀번호가_틀리면_401html로_리다이렉트한다() {
        // given
        Request request = mock(HttpRequest.class);
        Response response = new HttpResponse();
        LoginController loginController = new LoginController();
        InMemoryUserRepository.save(new User("로이스", "잘생김", "내마음속"));
        when(request.getAccount()).thenReturn("로이스");
        when(request.getPassword()).thenReturn("못생김");
        when(request.getVersionOfTheProtocol()).thenReturn("HTTP/1.1");

        // when
        loginController.doPost(request, response);

        // then
        assertThat(response.getStaticResourcePath()).isEqualTo("/401.html");
    }

    @Test
    void POST요청시_로그인에_성공하면_indexhtml로_리다이렉트한다() {
        // given
        Request request = mock(HttpRequest.class);
        Response response = new HttpResponse();
        LoginController loginController = new LoginController();
        InMemoryUserRepository.save(new User("로이스", "잘생김", "내마음속"));
        when(request.getAccount()).thenReturn("로이스");
        when(request.getPassword()).thenReturn("잘생김");
        when(request.getSession(true)).thenReturn(new Session("로이스"));
        when(request.getVersionOfTheProtocol()).thenReturn("HTTP/1.1");

        // when
        loginController.doPost(request, response);

        // then
        assertThat(response.getMessage()).hasToString(
                "HTTP/1.1 302 Found " + CRLF +
                        "Set-Cookie: JSESSIONID=로이스 " + CRLF +
                        "Location: /index.html " + CRLF
        );
    }
}
