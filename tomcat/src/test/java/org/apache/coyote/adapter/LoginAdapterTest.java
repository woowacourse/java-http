package org.apache.coyote.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.Protocol;
import org.apache.coyote.request.Request;
import org.apache.coyote.request.RequestLine;
import org.apache.coyote.response.HttpStatus;
import org.apache.coyote.view.Resource;
import org.apache.coyote.view.ViewResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginAdapterTest {

    @Test
    @DisplayName("로그인에 성공하면 index.html 페이지를 바디로 갖고, httpStatusCode가 302인 response를 생성한다.")
    void login_success() throws IOException, URISyntaxException {
        Map<String, String> queryString = new HashMap<>();
        queryString.put("account", "gugu");
        queryString.put("password", "password");

        Request request = new Request(
                new RequestLine(HttpMethod.GET, "/login", Protocol.HTTP1_1, queryString),
                ContentType.ALL);
        Path path = Path.of(ViewResource.class.getResource("/static/index.html").toURI());
        String expectedBody = new String(Files.readAllBytes(path));
        HttpStatus expectedStatus = HttpStatus.SUCCESS;

        Resource actual = new LoginAdapter().doHandle(request);

        assertAll(
                () -> assertThat(actual.getHttpStatus()).isEqualTo(expectedStatus),
                () -> assertThat(actual.getValue()).isEqualTo(expectedBody)
        );
    }

    @Test
    @DisplayName("로그인에 실패하면 401.html 페이지를 바디로 갖고, httpStatusCode가 401인 response를 생성한다.")
    void login_fail() throws IOException, URISyntaxException {
        Map<String, String> queryString = new HashMap<>();
        queryString.put("account", "gugu12");
        queryString.put("password", "password3232");

        Request request = new Request(
                new RequestLine(HttpMethod.GET, "/login", Protocol.HTTP1_1, queryString),
                ContentType.ALL);
        Path path = Path.of(ViewResource.class.getResource("/static/401.html").toURI());
        String expectedBody = new String(Files.readAllBytes(path));
        HttpStatus expectedStatus = HttpStatus.UNAUTHORIZED;

        Resource actual = new LoginAdapter().doHandle(request);

        assertAll(
                () -> assertThat(actual.getHttpStatus()).isEqualTo(expectedStatus),
                () -> assertThat(actual.getValue()).isEqualTo(expectedBody)
        );
    }
}
