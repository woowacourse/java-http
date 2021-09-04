package nextstep.jwp.controller;

import nextstep.jwp.MockSocket;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.session.HttpSession;
import nextstep.jwp.http.session.HttpSessions;
import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RegisterControllerTest {

    RegisterController registerController;

    @BeforeEach
    private void setUp() {
        registerController = new RegisterController(new UserService());
    }

    @DisplayName("/regiser GET매핑 200 ok을 반환한다.")
    @Test
    void doGet() throws IOException {
        MockSocket mockSocket = new MockSocket("GET /login HTTP/1.1 \r\n");
        HttpResponse response = new HttpResponse(mockSocket.getOutputStream());
        registerController.doGet(HttpRequest.of(mockSocket.getInputStream()), response);
        response.write();

        assertThat(mockSocket.output().split("\r\n")[0]).isEqualTo("HTTP/1.1 200 OK");
    }

    @DisplayName("회원가입 성공 시 index.html로 Redirect 한다.")
    @Test
    void doPost() throws IOException {
        String uuid = UUID.randomUUID().toString();
        MockSocket mockSocket = new MockSocket("POST /login HTTP/1.1 \r\n" +
                "Host: localhost:8080 \r\n" +
                "Content-Length: 46 \r\n" +
                "Connection-Type: application/x-www-form-urlencoded \r\n" +
                "Cookie: JSESSIONID=" + uuid + "\r\n" +
                "Accept: */* \r\n" +
                "\r\n" +
                "account=test&password=test&email=test@test.com");

        HttpResponse httpResponse = new HttpResponse(mockSocket.getOutputStream());
        registerController.doPost(HttpRequest.of(mockSocket.getInputStream()), httpResponse);
        httpResponse.write();
        String[] response = mockSocket.output().split("\r\n");
        assertThat(response[0]).isEqualTo("HTTP/1.1 302 Found");
        assertThat(response).contains("Location: /index.html");
    }

}