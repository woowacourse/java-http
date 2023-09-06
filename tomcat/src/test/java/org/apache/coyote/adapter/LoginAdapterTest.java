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
import org.apache.coyote.response.Response;
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
        byte[] expectedBody = (new String(Files.readAllBytes(path))).getBytes();
        byte[] expectedLine = "HTTP/1.1 302 SUCCESS".getBytes();

        Response actual = new LoginAdapter().doHandle(request);

        assertAll(
                () -> assertThat(actual.getResponseBytes()).startsWith(expectedLine),
                () -> assertThat(actual.getResponseBytes()).contains(expectedBody)
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
        byte[] expectedBody = (new String(Files.readAllBytes(path))).getBytes();
        byte[] expectedLine = "HTTP/1.1 401 UNAUTHOIRZED".getBytes();

        Response actual = new LoginAdapter().doHandle(request);

        assertAll(
                () -> assertThat(actual.getResponseBytes()).startsWith(expectedLine),
                () -> assertThat(actual.getResponseBytes()).contains(expectedBody)
        );
    }
}
