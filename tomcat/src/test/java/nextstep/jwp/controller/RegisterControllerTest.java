package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.catalina.Controller;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    private final Controller registerController = new RegisterController();

    @Test
    @DisplayName("RegisterController는 Post 요청시, 회원가입을 할 수 있다.")
    void registerControllerSuccessWhenPostMethod() throws IOException {
        //given
        HttpResponse response = HttpResponse.createEmptyResponse();

        //when
        registerController.service(RequestFixture.REGISTER_REQUEST, response);

        //then
        String actual = new String(response.getBytes());
        assertThat(InMemoryUserRepository.findByAccount("test")).isPresent();
        assertThat(actual).contains("HTTP/1.1 302 FOUND")
                .contains("Location: /index.html");
    }

    @Test
    @DisplayName("RegisterController GET 요청시, register.html을 반환한다.")
    void registerControllerSuccessServiceWhenGetMethod() throws IOException {
        //given
        HttpResponse response = HttpResponse.createEmptyResponse();
        URL url = getClass().getClassLoader().getResource("static/register.html");
        String expected = new String(Files.readAllBytes(Path.of(url.getPath())));

        //when
        registerController.service(RequestFixture.GET_REQUEST, response);

        //then
        String actual = new String(response.getBytes());
        assertThat(actual).contains("HTTP/1.1 200 OK")
                .contains(expected);
    }

}