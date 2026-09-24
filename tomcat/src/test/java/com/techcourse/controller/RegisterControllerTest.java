package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterControllerTest {

    private static final String ACCOUNT = "register-controller-user";

    private final RegisterController controller = new RegisterController();

    @Test
    void GET_요청이면_회원가입_페이지를_응답한다() throws Exception {
        final HttpRequest request = HttpRequest.from("GET /register HTTP/1.1");
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HttpResponse response = new HttpResponse(outputStream);

        controller.service(request, response);

        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .startsWith("HTTP/1.1 200 OK\r\n")
                .contains("<title>회원가입</title>");
    }

    @Test
    void POST_요청이면_회원을_저장하고_index로_리다이렉트한다() throws Exception {
        final String requestBody =
                "account=" + ACCOUNT + "&password=password&email=user%40example.com";
        final HttpRequest request = requestFrom(String.join("\r\n",
                "POST /register HTTP/1.1",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + requestBody.getBytes(StandardCharsets.UTF_8).length,
                "",
                requestBody));
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HttpResponse response = new HttpResponse(outputStream);

        controller.service(request, response);

        assertThat(InMemoryUserRepository.findByAccount(ACCOUNT)).isPresent();
        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .startsWith("HTTP/1.1 302 Found\r\n")
                .contains("Location: /index.html\r\n");
    }

    private HttpRequest requestFrom(final String httpRequest) throws IOException {
        return HttpRequest.from(new ByteArrayInputStream(httpRequest.getBytes(StandardCharsets.UTF_8)));
    }
}
