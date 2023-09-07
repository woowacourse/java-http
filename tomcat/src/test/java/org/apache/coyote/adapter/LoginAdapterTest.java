package org.apache.coyote.adapter;

import static org.apache.coyote.FixtureFactory.DEFAULT_HEADERS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.FixtureFactory;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.request.Request;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.response.HttpStatus;
import org.apache.coyote.view.Resource;
import org.apache.coyote.view.ViewResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginAdapterTest {

    @Test
    @DisplayName("로그인에 성공하면 index.html 페이지를 바디로 갖고, httpStatusCode가 302인 response를 생성한다.")
    void login_post_success() throws IOException, URISyntaxException {
        Map<String, String> body = new HashMap<>();
        body.put("account", "gugu");
        body.put("password", "password");
        Request request = FixtureFactory.getPostRequest("/login", DEFAULT_HEADERS,new RequestBody(body));

        Path path = Path.of(ViewResource.class.getResource("/static/index.html").toURI());
        String expectedBody = new String(Files.readAllBytes(path));
        HttpStatus expectedStatus = HttpStatus.SUCCESS;

        Resource actual = new LoginAdapter().adapt(request);

        assertAll(
                () -> assertThat(actual.getHttpStatus()).isEqualTo(expectedStatus),
                () -> assertThat(actual.getValue()).isEqualTo(expectedBody)
        );
    }

    @Test
    @DisplayName("로그인에 실패하면 401.html 페이지를 바디로 갖고, httpStatusCode가 401인 response를 생성한다.")
    void login_post_fail() throws IOException, URISyntaxException {
        Map<String, String> body = new HashMap<>();
        body.put("account", "gugu12");
        body.put("password", "password123");
        Request request = FixtureFactory.getPostRequest("/login", DEFAULT_HEADERS,new RequestBody(body));

        Path path = Path.of(ViewResource.class.getResource("/static/401.html").toURI());
        String expectedBody = new String(Files.readAllBytes(path));
        HttpStatus expectedStatus = HttpStatus.UNAUTHORIZED;

        Resource actual = new LoginAdapter().adapt(request);

        assertAll(
                () -> assertThat(actual.getHttpStatus()).isEqualTo(expectedStatus),
                () -> assertThat(actual.getValue()).isEqualTo(expectedBody)
        );
    }


    @Test
    @DisplayName("로그인 페이지에 접속할 수 있다")
    void login_get() throws IOException, URISyntaxException {
        Request request = FixtureFactory.getGetRequest("/login", DEFAULT_HEADERS);
        Path path = Path.of(ViewResource.class.getResource("/static/login.html").toURI());
        String expectedBody = new String(Files.readAllBytes(path));
        HttpStatus expectedStatus = HttpStatus.OK;

        Resource actual = new LoginAdapter().adapt(request);

        assertAll(
                () -> assertThat(actual.getHttpStatus()).isEqualTo(expectedStatus),
                () -> assertThat(actual.getValue()).isEqualTo(expectedBody)
        );
    }
}
