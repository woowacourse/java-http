package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;

import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.techcourse.db.InMemoryUserRepository;

class LoginControllerTest {

    private final Controller loginController = LoginController.getInstance();

    @DisplayName("올바른 회원 정보와 함께 로그인 요청이 오면 쿠키를 설정해주고 /index.html로 리다이렉트한다.")
    @Test
    void loginSuccess() throws Exception {
        // given
        final String httpRequestMessage = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=gugu&password=password ",
                "");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
        HttpRequest request = new HttpRequest(inputStream);
        OutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse response = new HttpResponse(outputStream);

        // when
        loginController.service(request, response);

        // then
        assertAll(
                () -> assertThat(outputStream.toString()).contains("HTTP/1.1 302 Found"),
                () -> assertThat(outputStream.toString()).contains("Set-Cookie: JSESSIONID="),
                () -> assertThat(outputStream.toString()).contains("Location: http://localhost:8080/index.html")
        );
        inputStream.close();
        outputStream.close();
    }

    @DisplayName("올바르지 않은 회원 정보와 함께 로그인 요청이 오면 /401.html로 리다이렉트한다.")
    @ParameterizedTest
    @CsvSource({
            "gugu, invalidPassword",
            "invalidAccount, password"
    })
    void loginFail(String account, String password) throws Exception {
        // given
        final String httpRequestMessage = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=" + account + "&password=" + password + " ",
                "");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
        HttpRequest request = new HttpRequest(inputStream);
        OutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse response = new HttpResponse(outputStream);

        // when
        loginController.service(request, response);

        // then
        String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: http://localhost:8080/401.html \r\n" +
                "\r\n";
        assertThat(outputStream.toString()).isEqualTo(expected);
        inputStream.close();
        outputStream.close();
    }

    @DisplayName("로그인 하지 않은 사용자가 GET /login 요청을 보내면 login.html을 응답한다.")
    @Test
    void loginPage() throws Exception {
        // given
        final String httpRequestMessage = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
        HttpRequest request = new HttpRequest(inputStream);
        OutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse response = new HttpResponse(outputStream);

        // when
        loginController.service(request, response);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        byte[] fileContent = Files.readAllBytes(new File(resource.getFile()).toPath());
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + fileContent.length + " \r\n" +
                "\r\n" +
                new String(fileContent) + "\r\n";
        assertThat(outputStream.toString()).isEqualTo(expected);
        inputStream.close();
        outputStream.close();
    }

    @DisplayName("이미 로그인이 되어있는데 GET /login 요청을 보내면 index.html로 리다이렉트한다.")
    @Test
    void alreadyLogin() throws Exception {
        // given
        Session session = new Session("656cef62-e3c4-40bc-a8df-94732920ed46");
        session.addAttribute("user", InMemoryUserRepository.findByAccount("gugu").get());
        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.add(session);

        final String httpRequestMessage = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                "");

        ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
        HttpRequest request = new HttpRequest(inputStream);
        OutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse response = new HttpResponse(outputStream);

        // when
        loginController.service(request, response);

        // then
        String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: http://localhost:8080/index.html \r\n" +
                "\r\n";
        assertThat(outputStream.toString()).isEqualTo(expected);
        inputStream.close();
        outputStream.close();
    }
}
