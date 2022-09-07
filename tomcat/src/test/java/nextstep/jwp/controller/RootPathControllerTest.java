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

class RootPathControllerTest {

    private final Controller controller = new RootPathController();

    @DisplayName("처리할 수 있는 요청인지 반환한다.")
    @Test
    void isSuitable() throws IOException {
        String request = String.join("\r\n",
                "GET / HTTP/1.1\n" +
                        "Host: localhost:8080\n" +
                        "Connection: keep-alive\n");
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.from(bufferedReader);

        boolean actual = controller.isSuitable(httpRequest);

        assertThat(actual).isTrue();
    }

    @DisplayName("루트 페이지를 반환한다.")
    @Test
    void service_Get() throws IOException {
        String request = String.join("\r\n",
                "GET / HTTP/1.1\n" +
                        "Host: localhost:8080\n" +
                        "Connection: keep-alive\n");
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.from(bufferedReader);
        HttpResponse httpResponse = HttpResponse.getNew();

        controller.service(httpRequest, httpResponse);

        assertThat(httpResponse.getResponse())
                .contains("Hello world!")
                .contains("Content-Type: text/plain;charset=utf-8");
    }
}