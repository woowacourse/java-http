package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import java.util.stream.Stream;
import org.apache.coyote.http11.httpmessage.request.QueryString;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.httpmessage.response.StatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HandlerTest {

    @Test
    @DisplayName("handlerMapping이 존재하지 않으면 404.html을 제공한다.")
    void not_found_response() throws IOException {
        // given
        final Handler handler = new Handler(null, null);

        // when
        final HttpResponse result = handler.makeResponse();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        final String expectBody = new String(
            Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath()));

        assertThat(result.getBody()).isEqualTo(expectBody);
        assertThat(result.getStatusCode()).isEqualTo(StatusCode.NOT_FOUND);
        assertThat(result.getHttpHeader().getHeader().get("Content-Type")).isEqualTo("text/html");
        assertThat(result.getHttpHeader().getHeader().get("Content-Length"))
            .isEqualTo(String.valueOf(expectBody.getBytes().length));
    }

    @Test
    @DisplayName("handlerMapping이 main 요청이면 Hello world!를 반환한다.")
    void main_response() throws IOException {
        // given
        final Handler handler = new Handler(HandlerMapping.MAIN, null);

        // when
        final HttpResponse result = handler.makeResponse();

        // then
        final String expectBody = "Hello world!";

        assertThat(result.getBody()).isEqualTo(expectBody);
        assertThat(result.getStatusCode()).isEqualTo(StatusCode.OK);
        assertThat(result.getHttpHeader().getHeader().get("Content-Type")).contains("text/html");
        assertThat(result.getHttpHeader().getHeader().get("Content-Length"))
            .isEqualTo(String.valueOf(expectBody.length()));
    }

    @Test
    @DisplayName("handlerMapping이 index.html요청이면 index.html을 반환한다.")
    void index_html_response() throws IOException {
        // given
        final Handler handler = new Handler(HandlerMapping.INDEX, null);

        // when
        final HttpResponse result = handler.makeResponse();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String expectBody = new String(
            Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath()));

        assertThat(result.getBody()).isEqualTo(expectBody);
        assertThat(result.getStatusCode()).isEqualTo(StatusCode.OK);
        assertThat(result.getHttpHeader().getHeader().get("Content-Type")).contains("text/html");
        assertThat(result.getHttpHeader().getHeader().get("Content-Length"))
            .isEqualTo(String.valueOf(expectBody.getBytes().length));
    }

    @Test
    @DisplayName("handlerMapping이 /login요청이면 login.html을 반환한다.")
    void login_response() throws IOException {
        // given
        final Handler handler = new Handler(HandlerMapping.LOGIN, null);

        // when
        final HttpResponse result = handler.makeResponse();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final String expectBody = new String(
            Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath()));

        assertThat(result.getBody()).isEqualTo(expectBody);
        assertThat(result.getStatusCode()).isEqualTo(StatusCode.OK);
        assertThat(result.getHttpHeader().getHeader().get("Content-Type")).contains("text/html");
        assertThat(result.getHttpHeader().getHeader().get("Content-Length"))
            .isEqualTo(String.valueOf(expectBody.getBytes().length));
    }

    @Test
    @DisplayName("로그인한 사용자가 서비스 회원이면 index.html로 리다이랙트 합니다.")
    void login_success_response() throws IOException {
        // given
        final String[] queryStrings = new String[2];
        queryStrings[0] = "account=gugu";
        queryStrings[1] = "password=password";
        final QueryString queryString = QueryString.fromRequest(queryStrings);

        final Handler handler = new Handler(HandlerMapping.LOGIN, queryString);

        // when
        final HttpResponse result = handler.makeResponse();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final String expectBody = new String(
            Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath()));

        assertThat(result.getBody()).isEqualTo(expectBody);
        assertThat(result.getStatusCode()).isEqualTo(StatusCode.REDIRECT);
        assertThat(result.getHttpHeader().getHeader().get("Content-Type")).contains("text/html");
        assertThat(result.getHttpHeader().getHeader().get("Content-Length"))
            .isEqualTo(String.valueOf(expectBody.getBytes().length));
        assertThat(result.getHttpHeader().getHeader().get("Location")).isEqualTo("/index.html");
    }

    @ParameterizedTest
    @MethodSource("getWrongMember")
    @DisplayName("로그인한 사용자가 서비스 회원이 아니면 401.html로 리다이랙트 합니다.")
    void login_fail_response(final String account, final String password) throws IOException {
        // given
        final String[] queryStrings = new String[2];
        queryStrings[0] = "account=" + account;
        queryStrings[1] = "password=" + password;
        final QueryString queryString = QueryString.fromRequest(queryStrings);

        final Handler handler = new Handler(HandlerMapping.LOGIN, queryString);

        // when
        final HttpResponse result = handler.makeResponse();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final String expectBody = new String(
            Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath()));

        assertThat(result.getBody()).isEqualTo(expectBody);
        assertThat(result.getStatusCode()).isEqualTo(StatusCode.REDIRECT);
        assertThat(result.getHttpHeader().getHeader().get("Content-Type")).contains("text/html");
        assertThat(result.getHttpHeader().getHeader().get("Content-Length"))
            .isEqualTo(String.valueOf(expectBody.getBytes().length));
        assertThat(result.getHttpHeader().getHeader().get("Location")).isEqualTo("/401.html");
    }

    private static Stream<Arguments> getWrongMember() {
        return Stream.of(
            Arguments.of("gugu", "password1234"),
            Arguments.of("ako", "password"),
            Arguments.of("ako", "password1234")
        );
    }
}
