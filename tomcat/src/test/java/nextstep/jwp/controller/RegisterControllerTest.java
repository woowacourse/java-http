package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RegisterControllerTest {

    @DisplayName("Get 요청을 보내면 회원가입 페이지를 보여준다.")
    @Test
    void processGet() throws IOException {
        // given
        final Controller controller = new RegisterController();
        final String firstLine = "GET /register HTTP/1.1 ";
        final List<String> lines = List.of("Host: localhost:8080 ", "Connection: keep-alive ");
        final HttpRequest httpRequest = HttpRequest.from(firstLine, lines, null);
        final HttpResponse httpResponse = new HttpResponse();

        // when
        controller.process(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.OK);
    }

    @DisplayName("회원가입 요청이 완료되었을 때 상태코드 302와 index.html 경로를 헤더로 보낸다.")
    @Test
    void processPost() throws IOException {
        // given
        final Controller controller = new RegisterController();
        final String firstLine = "POST /register HTTP/1.1 ";
        final String queryString = "account=gugu&password=password";
        final List<String> lines = List.of("Host: localhost:8080 ", "Connection: keep-alive ",
                "Content-Length: " + queryString.length());
        final InputStream is = new ByteArrayInputStream(queryString.getBytes());
        final BufferedReader br = new BufferedReader(new InputStreamReader(is));
        final HttpRequest httpRequest = HttpRequest.from(firstLine, lines, br);
        final HttpResponse httpResponse = new HttpResponse();

        // when
        controller.process(httpRequest, httpResponse);

        // then
        assertAll(
                () -> assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(httpResponse.getHttpHeaders().getLocation()).isEqualTo("/index.html")
        );
    }
}
