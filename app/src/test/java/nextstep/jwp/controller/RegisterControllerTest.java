package nextstep.jwp.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterControllerTest extends ControllerTest {

    @DisplayName("GET /register 요청 시 register.html를 응답한다.")
    @Test
    void doGet() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                "");

        // when
        sendRequest(httpRequest);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 4319 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("POST /register 요청 성공시 index.html로 리다이렉트한다.")
    @Test
    void doPost() {
        // given
        String requestBody = "account=amazzi&password=password&email=hkkang%40woowahan.com";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.getBytes().length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                requestBody);

        // when
        sendRequest(httpRequest);

        // then
        String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: http://localhost:8080/index.html \r\n";

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("POST /register 요청 실패시 register.html로 리다이렉트한다.")
    @Test
    void doPostFail() {
        // given
        String requestBody = "account=admin&password=password&email=hkkang%40woowahan.com";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.getBytes().length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                requestBody);

        // when
        sendRequest(httpRequest);

        // then
        String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: http://localhost:8080/register.html \r\n" ;

        assertThat(socket.output()).isEqualTo(expected);
    }
}
