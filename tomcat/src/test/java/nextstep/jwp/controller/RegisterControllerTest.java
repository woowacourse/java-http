package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RegisterControllerTest {

    private final Controller controller = new RegisterController();

    @DisplayName("처리할 수 있는 요청인지 반환한다.")
    @ParameterizedTest
    @CsvSource({"GET /register, true", "POST /register, true", "GET /index.html, false"})
    void isSuitable(String methodAndPath, boolean expected) throws IOException {
        String request = String.join("\r\n",
                methodAndPath + " HTTP/1.1\n" +
                        "Host: localhost:8080\n" +
                        "Connection: keep-alive\n");
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.from(bufferedReader);

        boolean actual = controller.isSuitable(httpRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("회원가입 페이지를 반환한다.")
    @Test
    void service_Get() throws IOException {
        String request = String.join("\r\n",
                "GET /register HTTP/1.1\n" +
                        "Host: localhost:8080\n" +
                        "Connection: keep-alive\n");
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.from(bufferedReader);
        HttpResponse httpResponse = HttpResponse.getNew();

        controller.service(httpRequest, httpResponse);

        assertThat(httpResponse.getResponse())
                .contains("200 OK")
                .contains("Content-Type: text/html;charset=utf-8")
                .contains("Content-Length: 4319");
    }

    @DisplayName("회원 가입을 한다.")
    @Test
    void service_Post() throws IOException {
        String request = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 49",
                "",
                "account=chris&password=password&email=abc@abc.com");
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.from(bufferedReader);
        HttpResponse httpResponse = HttpResponse.getNew();

        controller.service(httpRequest, httpResponse);

        assertThat(httpResponse.getResponse())
                .contains("302 Found")
                .contains("Location: http://localhost:8080/index.html");
    }
}
