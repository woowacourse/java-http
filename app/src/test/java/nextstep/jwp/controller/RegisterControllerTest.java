package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.TestFixture;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.ResponseHeaders;
import nextstep.jwp.http.request.HttpMethod;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RegisterControllerTest {

    private final Controller controller = new RegisterController();

    @DisplayName("회원가입 페이지 - 응답 성공")
    @Test
    public void doGet() throws Exception {
        //given
        final HttpRequest httpRequest = TestFixture.getHttpRequest(HttpMethod.GET, "register");
        final HttpResponse httpResponse = new HttpResponse(new ResponseHeaders());

        // when
        controller.service(httpRequest, httpResponse);

        //then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        final String file = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(httpResponse.getBody()).isEqualTo(file);
    }

    @DisplayName("회원가입 성공 - index 로 리다이렉트")
    @Test
    public void doPost() throws Exception {
        //given
        final HttpRequest httpRequest = TestFixture.getHttpRequest(HttpMethod.POST, "register",
                "account=solong&password=1234&email=solong@email");
        final HttpResponse httpResponse = new HttpResponse(new ResponseHeaders());

        // when
        controller.service(httpRequest, httpResponse);

        //then
        assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.FOUND);
        assertThat(httpResponse.getBody()).isEqualTo("/index.html");
    }

    @DisplayName("회원가입 실패 - 이미 존재하는 유저")
    @Test
    public void doPost_fail() throws Exception {
        //given
        final User user = new User("solong", "1234", "solong@email");
        InMemoryUserRepository.save(user);

        final HttpRequest httpRequest = TestFixture.getHttpRequest(HttpMethod.POST, "register",
                "account=solong&password=1234&email=solong@email");
        final HttpResponse httpResponse = new HttpResponse(new ResponseHeaders());

        // when
        controller.service(httpRequest, httpResponse);

        //then
        assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        final String file = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(httpResponse.getBody()).isEqualTo(file);
    }
}
