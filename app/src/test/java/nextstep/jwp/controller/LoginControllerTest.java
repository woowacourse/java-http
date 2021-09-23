package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.TestFixture;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.ResponseHeaders;
import nextstep.jwp.http.request.HttpMethod;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    private final Controller controller = new LoginController();

    @DisplayName("로그인 페이지 - 응답 성공")
    @Test
    void doGet() throws Exception {
        //given
        final HttpRequest httpRequest = TestFixture.getHttpRequest(HttpMethod.GET, "login");
        final HttpResponse httpResponse = new HttpResponse(new ResponseHeaders());

        HttpSession httpSession = httpRequest.getSession();
        httpSession.removeAttribute("user");

        //when
        controller.service(httpRequest, httpResponse);

        //then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final String file = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(httpResponse.getBody()).isEqualTo(file);
    }

    @DisplayName("로그인 페이지 - 이미 로그인이 되어있을 때 index 로")
    @Test
    void doGet_isLoggedIn() throws Exception {
        //given
        final HttpRequest httpRequest = TestFixture
                .getHttpRequest(HttpMethod.GET, "login", new HttpCookie());
        HttpSession httpSession = httpRequest.getSession();
        httpSession.setAttribute("user", new User("solong", "1234", "1234@mail"));
        final HttpResponse httpResponse = new HttpResponse(new ResponseHeaders());

        //when
        controller.service(httpRequest, httpResponse);

        //then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String file = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(httpResponse.getBody()).isEqualTo(file);
    }

    @DisplayName("로그인 성공 - index 로 리다이렉트")
    @Test
    void doPost() throws Exception {
        //given
        final User user = new User("solong", "1234", "solong@email");
        InMemoryUserRepository.save(user);

        final HttpRequest httpRequest = TestFixture
                .getHttpRequest(HttpMethod.POST, "login", "account=solong&password=1234");
        final HttpResponse httpResponse = new HttpResponse(new ResponseHeaders());

        //when
        controller.service(httpRequest, httpResponse);

        //then
        assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.FOUND);
        assertThat(httpResponse.getBody()).isEqualTo("/index.html");
    }

    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    @Test
    void doPost_fail() throws Exception {
        //given
        final User user = new User("solong", "1234", "solong@email");
        InMemoryUserRepository.save(user);

        final HttpRequest httpRequest = TestFixture
                .getHttpRequest(HttpMethod.POST, "login", "account=solong&password=123");
        final HttpResponse httpResponse = new HttpResponse(new ResponseHeaders());

        //when
        controller.service(httpRequest, httpResponse);

        //then
        assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}