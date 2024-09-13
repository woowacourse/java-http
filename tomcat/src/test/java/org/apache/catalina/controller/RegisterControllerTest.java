package org.apache.catalina.controller;

import static org.apache.coyote.http11.Http11HeaderName.LOCATION;
import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.StatusLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    private final RegisterController registerController = new RegisterController();

    @DisplayName("GET 요청 시 register.html을 반환해야 한다.")
    @Test
    void doGet() throws IOException {
        String requestString = String.join("\r\n",
                "GET /register HTTP/1.1",
                "Host: localhost",
                "Content-Length: 0",
                "Content-Type: application/x-www-form-urlencoded",
                ""
        );

        InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
        Http11Request request = Http11Request.from(inputStream);
        Http11Response response = Http11Response.of(request);

        registerController.doGet(request, response);
        StatusLine statusLine = response.getStatusLine();

        String path = "src/main/resources/static/register.html";
        String htmlString = new String(Files.readAllBytes(Paths.get(path)));

        assertThat(statusLine.getStatusCode()).isEqualTo(StatusCode.valueOf(200));
        assertThat(response.getResponseBody().getBody()).isEqualTo(htmlString);
    }

    @DisplayName("POST 요청 시 이미 존재하는 계정으로 가입할 경우 302 리다이렉트된다.")
    @Test
    void doPost_ExistingAccount() throws IOException {
        String existingAccount = "testUser";
        String existingPassword = "password";
        String existingEmail = "test@example.com";
        InMemoryUserRepository.save(new User(existingAccount, existingPassword, existingEmail));

        String body = "account=" + existingAccount + "&password=" + existingPassword + "&email=" + existingEmail;
        String requestString = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body
        );

        InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
        Http11Request request = Http11Request.from(inputStream);
        Http11Response response = Http11Response.of(request);

        registerController.doPost(request, response);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(StatusCode.valueOf(302));
        assertThat(response.getResponseHeader().getHeader("Location")).isEqualTo("/register");
    }

    @DisplayName("POST 요청 시 새로운 계정으로 가입할 경우 index.html로 302 리다이렉트된다.")
    @Test
    void doPost_NewAccount() throws IOException {
        String newAccount = "newUser";
        String newPassword = "newPassword";
        String newEmail = "new@example.com";
        String body = "account=" + newAccount + "&password=" + newPassword + "&email=" + newEmail;
        String requestString = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body
        );

        InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
        Http11Request request = Http11Request.from(inputStream);
        Http11Response response = Http11Response.of(request);

        registerController.doPost(request, response);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(StatusCode.valueOf(302));
        assertThat(response.getResponseHeader().getHeader("Location")).isEqualTo("/index.html");
    }

    @DisplayName("POST 요청 시 유효하지 않은 입력으로 가입할 경우 302 리다이렉트된다.")
    @Test
    void doPost_InvalidInput() throws IOException {
        String body = "account=&password=1234&email=invalidEmail";
        String requestString = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body
        );

        InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
        Http11Request request = Http11Request.from(inputStream);
        Http11Response response = Http11Response.of(request);

        registerController.doPost(request, response);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(StatusCode.valueOf(302));
        assertThat(response.getResponseHeader().getHeader(LOCATION.getName())).isEqualTo("/register");
    }
}
