package org.apache.coyote.http11;

import com.techcourse.controller.Controller;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticPageController;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestMapperTest {

    @Test
    @DisplayName("로그인 경로를 요청하면 LoginController를 반환한다.")
    void should_map_login_controller_when_login_path_requested() throws IOException {
        // given
        String request = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequestParser.getInstance().parseRequest(bufferedReader);

        // when
        Controller mappedController = RequestMapper.getInstance().mapRequest(httpRequest);

        //then
        assertThat(mappedController).isEqualTo(LoginController.getInstance());
    }

    @Test
    @DisplayName("회원가입 경로를 요청하면 RegisterController를 반환한다.")
    void should_map_register_controller_when_register_path_requested() throws IOException {
        // given
        String request = String.join("\r\n",
                "GET /register HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequestParser.getInstance().parseRequest(bufferedReader);

        // when
        Controller mappedController = RequestMapper.getInstance().mapRequest(httpRequest);

        //then
        assertThat(mappedController).isEqualTo(RegisterController.getInstance());
    }

    @Test
    @DisplayName("정적 파일 경로를 요청하면 StaticController를 반환한다.")
    void should_map_static_controller_when_static_path_requested() throws IOException {
        // given
        String request = String.join("\r\n",
                "GET / HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequestParser.getInstance().parseRequest(bufferedReader);

        // when
        Controller mappedController = RequestMapper.getInstance().mapRequest(httpRequest);

        //then
        assertThat(mappedController).isEqualTo(StaticPageController.getInstance());
    }
}
