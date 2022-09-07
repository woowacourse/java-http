package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

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

public class LoginControllerTest {

    @DisplayName("로그인 기능을 성공하는 경우에 상태코드 302와 index.html 경로를 헤더로 보낸다.")
    @Test
    void processPost_success_login() throws IOException {
        // given
        final LoginController loginController = new LoginController();
        final String firstLine = "POST /login HTTP/1.1 ";
        final List<String> lines = List.of("Host: localhost:8080 ", "Connection: keep-alive ", "Content-Length: 30");
        final String queryString = "account=gugu&password=password";
        final InputStream is = new ByteArrayInputStream(queryString.getBytes());
        final BufferedReader br = new BufferedReader(new InputStreamReader(is));
        final HttpRequest httpRequest = HttpRequest.from(firstLine, lines, br);
        final HttpResponse httpResponse = new HttpResponse();

        // when
        loginController.processPost(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.FOUND);
        assertThat(httpResponse.getHttpHeaders().getLocation()).isEqualTo("/index.html");

    }

    @DisplayName("로그인 기능을 실패하는 경우에 상태코드 302와 404.html 경로를 헤더로 보낸다.")
    @Test
    void processPost_fail_login() throws IOException {
        // given
        final LoginController loginController = new LoginController();
        final String firstLine = "POST /login HTTP/1.1 ";
        final List<String> lines = List.of("Host: localhost:8080 ", "Connection: keep-alive ", "Content-Length: 31");
        final String queryString = "account=gugu&password=password1";
        final InputStream is = new ByteArrayInputStream(queryString.getBytes());
        final BufferedReader br = new BufferedReader(new InputStreamReader(is));
        final HttpRequest httpRequest = HttpRequest.from(firstLine, lines, br);
        final HttpResponse httpResponse = new HttpResponse();

        // when
        loginController.processPost(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.FOUND);
        assertThat(httpResponse.getHttpHeaders().getLocation()).isEqualTo("/404.html");
    }
}
