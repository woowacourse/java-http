package org.apache.catalina.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.RequestUri;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestMappingTest {

    private final RequestMapping requestMapping = RequestMapping.from(
            Map.of(
                    RequestUri.from("/login"), new LoginController(),
                    RequestUri.from("/register"), new RegisterController()
            )
    );

    @DisplayName("요청이 /login 이면 LoginController를 반환한다.")
    @Test
    void getLoginController() throws IOException {
        String requestString = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost",
                "Content-Length: 0",
                "Content-Type: application/x-www-form-urlencoded",
                ""
        );
        InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
        Http11Request request = Http11Request.from(inputStream);

        Controller controller = requestMapping.getController(request);

        assertThat(controller instanceof LoginController).isTrue();
    }

    @DisplayName("요청이 /register 이면 RegisterController를 반환한다.")
    @Test
    void getRegisterController() throws IOException {
        String requestString = String.join("\r\n",
                "GET /register HTTP/1.1",
                "Host: localhost",
                "Content-Length: 0",
                "Content-Type: application/x-www-form-urlencoded",
                ""
        );
        InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
        Http11Request request = Http11Request.from(inputStream);

        Controller controller = requestMapping.getController(request);

        assertThat(controller instanceof RegisterController).isTrue();
    }

    @DisplayName("요청이 /register, /login 이 아니면 StaticController를 반환한다.")
    @Test
    void getStaticController() throws IOException {
        String requestString = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost",
                "Content-Length: 0",
                "Content-Type: application/x-www-form-urlencoded",
                ""
        );
        InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
        Http11Request request = Http11Request.from(inputStream);

        Controller controller = requestMapping.getController(request);

        assertThat(controller instanceof StaticController).isTrue();
    }
}
