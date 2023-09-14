package org.apache.controller;

import static org.apache.coyote.FixtureFactory.DEFAULT_HEADERS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.fileReader.FileReader;
import nextstep.jwp.controller.RegisterController;
import org.apache.coyote.FixtureFactory;
import org.apache.coyote.http11.Protocol;
import org.apache.coyote.request.Request;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    @Test
    @DisplayName("/register로 접속해서 POST 요청을 보내면 회원가입할 수 있다")
    void register_post() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("account", "kong");
        body.put("password", "password");
        body.put("email", "kong@kong");

        Request request = FixtureFactory.getHttpPostRequest("/register", DEFAULT_HEADERS, new RequestBody(body));
        Response response = new Response(Protocol.HTTP1_1);

        RegisterController registerController = new RegisterController();
        registerController.service(request, response);

        String expectedLine = "HTTP/1.1 302 FOUND";
        String expectedHeader = "Location: /index.html";

        assertAll(
                () -> assertThat(response.getResponseBytes()).startsWith(expectedLine.getBytes()),
                () -> assertThat(response.getResponseBytes()).contains(expectedHeader.getBytes())
        );
    }

    @Test
    @DisplayName("/register로 접속해서 GET 요청을 보내면 회원가입 페이지를 확인할 수 있다")
    void register_get() throws Exception {
        Request request = FixtureFactory.getHttpGetRequest("/register", DEFAULT_HEADERS);
        Response response = new Response(Protocol.HTTP1_1);

        RegisterController registerController = new RegisterController();
        registerController.service(request, response);

        Path path = Path.of(FileReader.class.getResource("/static/register.html").toURI());
        String expectedLine = "HTTP/1.1 200 OK";
        String expectedBody = new String(Files.readAllBytes(path));

        assertAll(
                () -> assertThat(response.getResponseBytes()).startsWith(expectedLine.getBytes()),
                () -> assertThat(response.getResponseBytes()).contains(expectedBody.getBytes())
        );
    }
}
