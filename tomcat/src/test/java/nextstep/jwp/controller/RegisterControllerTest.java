package nextstep.jwp.controller;

import common.http.Request;
import common.http.Response;
import common.http.Session;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RegisterControllerTest {

    @Test
    void GET요청시_registerhtml의_경로를_응답에_실는다() {
        // given
        Request request = mock(HttpRequest.class);
        Response response = new HttpResponse();
        RegisterController registerController = new RegisterController();

        when(request.getVersionOfTheProtocol()).thenReturn("HTTP/1.1");

        // when
        registerController.doGet(request, response);

        // then
        assertThat(response.getStaticResourcePath()).isEqualTo("/register.html");
    }

    @Test
    void POST요청시_이미_가입된_회원이면_예외를_반환한다() {
        // given
        InMemoryUserRepository.save(new User("로이스", "잘생김", "내마음속"));
        Request request = mock(HttpRequest.class);
        Response response = new HttpResponse();
        RegisterController registerController = new RegisterController();

        when(request.getVersionOfTheProtocol()).thenReturn("HTTP/1.1");
        when(request.getAccount()).thenReturn("로이스");

        // expect
        assertThatThrownBy(() -> registerController.doPost(request, response))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 가입된 회원입니다.");
    }

    @Test
    void POST요청시_회원가입을_진행한다() {
        // given
        Request request = mock(HttpRequest.class);
        Response response = new HttpResponse();
        RegisterController registerController = new RegisterController();

        when(request.getVersionOfTheProtocol()).thenReturn("HTTP/1.1");
        when(request.getAccount()).thenReturn("롤스로이스");
        when(request.getPassword()).thenReturn("비쌈");
        when(request.getEmail()).thenReturn("근데내꺼ㅎ");
        when(request.getSession(true)).thenReturn(new Session("id"));
        // when
        registerController.doPost(request, response);

        // then
        User user = InMemoryUserRepository.findByAccount("롤스로이스").get();
        assertThat(user.checkPassword("비쌈")).isTrue();
        assertThat(response.toString()).contains("Location: /index.html");
    }
}
