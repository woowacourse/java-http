package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.coyote.http11.InputReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterControllerTest {

    @Test
    @DisplayName("회원가입 성공 시 기본 페이지로 리다이렉트된다.")
    void process() throws IOException {
        RegisterController controller = new RegisterController();
        String body = "account=1234&password=1234&email=1234%40email.com";
        String request = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
        HttpRequest httpRequest = getHttpRequest(request);

        HttpResponse httpResponse = controller.process(httpRequest);

        assertThat(InMemoryUserRepository.findByAccount("1234")).isPresent();
        assertThat(httpResponse.getLocation()).isEqualTo("/index.html");
    }

    private HttpRequest getHttpRequest(String request) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        InputReader inputReader = new InputReader(inputStream);

        return new HttpRequest(inputReader);
    }
}
