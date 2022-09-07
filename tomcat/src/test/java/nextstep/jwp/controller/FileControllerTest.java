package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

class FileControllerTest {

    private final Controller controller = new FileController();

    @DisplayName("처리할 수 있는 요청인지 반환한다.")
    @ParameterizedTest
    @CsvSource({".html, true", ".css, true", ".js, true", ".abc, false"})
    void isSuitable(String path, boolean expected) throws IOException {
        String request = String.join("\r\n",
                "GET " + path + " HTTP/1.1\n" +
                        "Host: localhost:8080\n" +
                        "Connection: keep-alive\n");
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.from(bufferedReader);

        boolean actual = controller.isSuitable(httpRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("요청을 처리한다.")
    @ParameterizedTest
    @MethodSource("providePathAndExpectedResponse")
    void service(String path, String expectedContentType, String expectedContentLength) throws IOException {
        String request = String.join("\r\n",
                "GET " + path + " HTTP/1.1\n" +
                        "Host: localhost:8080\n" +
                        "Connection: keep-alive\n");
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.from(bufferedReader);
        HttpResponse httpResponse = HttpResponse.getNew();

        controller.service(httpRequest, httpResponse);

        assertThat(httpResponse.getResponse())
                .contains("200 OK")
                .contains("Content-Type: " + expectedContentType)
                .contains("Content-Length: " + expectedContentLength);
    }

    private static Stream<Arguments> providePathAndExpectedResponse() {
        return Stream.of(
                Arguments.of(
                        "/index.html", "text/html;charset=utf-8 ", "5564"
                ),
                Arguments.of(
                        "/css/styles.css", "text/css;charset=utf-8 ", "211991"
                ),
                Arguments.of(
                        "/js/scripts.js", "text/js;charset=utf-8 ", "976"
                )
        );
    }

    @DisplayName("존재하지 않는 파일을 요청하면 404.html으로 redirect 시킨다.")
    @Test
    void service_RedirectTo404Html() throws IOException {
        String request = String.join("\r\n",
                "GET /iiiindex.html HTTP/1.1\n" +
                        "Host: localhost:8080\n" +
                        "Connection: keep-alive\n");
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.from(bufferedReader);
        HttpResponse httpResponse = HttpResponse.getNew();

        controller.service(httpRequest, httpResponse);

        assertThat(httpResponse.getResponse())
                .contains("302 Found")
                .contains("Location: http://localhost:8080/404.html");
    }
}