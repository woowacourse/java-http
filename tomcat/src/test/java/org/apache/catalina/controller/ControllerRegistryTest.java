package org.apache.catalina.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootController;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.apache.coyote.http.request.HttpRequest;
import org.junit.jupiter.api.Test;

public class ControllerRegistryTest {

    @Test
    public void 로그인_요청이_들어오면_로그인_컨트롤러를_가져온다() throws IOException {
        // Given
        String httpRequestString = "GET /login HTTP/1.1\r\nHost: localhost\r\n\r\n";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(httpRequestString));
        HttpRequest request = new HttpRequest(bufferedReader);

        // When
        Controller controller = ControllerRegistry.getController(request);

        // Then
        assertAll(
                () -> assertNotNull(controller),
                () -> assertThat(controller).isInstanceOf(LoginController.class)
        );
    }

    @Test
    public void 회원가입_요청이_들어오면_회원가입_컨트롤러를_가져온다() throws IOException {
        // Given
        String httpRequestString = "GET /register HTTP/1.1\r\nHost: localhost\r\n\r\n";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(httpRequestString));
        HttpRequest request = new HttpRequest(bufferedReader);

        // When
        Controller controller = ControllerRegistry.getController(request);

        // Then
        assertAll(
                () -> assertNotNull(controller),
                () -> assertThat(controller).isInstanceOf(RegisterController.class)
        );
    }

    @Test
    public void 루트_요청이_들어오면_루트_컨트롤러를_가져온다() throws IOException {
        // Given
        String httpRequestString = "GET / HTTP/1.1\r\nHost: localhost\r\n\r\n";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(httpRequestString));
        HttpRequest request = new HttpRequest(bufferedReader);

        // When
        Controller controller = ControllerRegistry.getController(request);

        // Then
        assertAll(
                () -> assertNotNull(controller),
                () -> assertThat(controller).isInstanceOf(RootController.class)
        );
    }

    @Test
    public void 매핑되지_않은_요청이_들어오면_기본_컨트롤러를_가져온다() throws IOException {
        // Given
        String httpRequestString = "GET /unknown HTTP/1.1\r\nHost: localhost\r\n\r\n";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(httpRequestString));
        HttpRequest request = new HttpRequest(bufferedReader);

        // When
        Controller controller = ControllerRegistry.getController(request);

        // Then
        assertAll(
                () -> assertNotNull(controller),
                () -> assertThat(controller).isInstanceOf(StaticResourceController.class)
        );
    }
}
