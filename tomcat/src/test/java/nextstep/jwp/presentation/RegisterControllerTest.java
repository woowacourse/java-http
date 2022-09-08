package nextstep.jwp.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    private final RegisterController registerController = RegisterController.instance();

    @Test
    void 회원가입_GET_테스트() throws Exception {
        // given
        String request = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(request.getBytes())));
        HttpRequest httpRequest = HttpRequest.parse(bufferedReader);

        // when
        HttpResponse httpResponse = registerController.service(httpRequest);

        // then
        URL resource = getClass().getClassLoader().getResource("static/register.html");
        assert resource != null;
        String expectedResponseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String actual = httpResponse.toResponseFormat();
        assertAll(
                () -> assertThat(actual).contains("HTTP/1.1 200 OK"),
                () -> assertThat(actual).contains(expectedResponseBody)
        );
    }

    @Test
    void 회원가입_POST_테스트() throws Exception {
        // given
        String requestBody = "account=account&password=password&email=account@woowahan.com";
        String request = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.getBytes().length,
                "",
                requestBody);
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(request.getBytes())));
        HttpRequest httpRequest = HttpRequest.parse(bufferedReader);

        // when
        HttpResponse httpResponse = registerController.service(httpRequest);

        // then
        String actual = httpResponse.toResponseFormat();
        assertAll(
                () -> assertThat(actual).contains("HTTP/1.1 302 Found"),
                () -> assertThat(actual).contains("Location: /index.html")
        );
    }
}
