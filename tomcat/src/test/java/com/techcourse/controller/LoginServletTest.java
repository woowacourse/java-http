package com.techcourse.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.resource.ResourceParser;
import org.junit.jupiter.api.Test;
import support.TestHttpUtils;

class LoginServletTest {

    @Test
    void 요청_URI가_login이고_GET_메서드라면_로그인_페이지로_이동한다() throws IOException {
        final var result = TestHttpUtils.sendGet("http://127.0.0.1:8080", "/login");

        File loginPage = ResourceParser.getRequestFile("/login.html");
        String expectBody = new String(Files.readAllBytes(loginPage.toPath()));

        assertThat(result.body()).isEqualTo(expectBody);
        assertThat(result.statusCode()).isEqualTo(200);
    }

    @Test
    void 요청_URI가_login이고_POST_메서드라면_로그인을_수행후_메인페이지로_리다이렉트한다() {
        String body = "account=gugu&password=password";
        final var result = TestHttpUtils.sendPost("http://127.0.0.1:8080", "/login", body);

        assertThat(result.statusCode()).isEqualTo(302);
        assertThat(result.headers().map().containsKey("Location")).isTrue();
        assertThat(result.headers().map().get("Location")).isEqualTo(List.of("/index.html"));
    }

    @Test
    void 요청_URI가_login이고_POST_메서드일때_로그인_실패시_에러페이지로_이동한다() throws IOException {
        String body = "account=notExist&password=password";
        final var result = TestHttpUtils.sendPost("http://127.0.0.1:8080", "/login", body);

        File errorPage = ResourceParser.getRequestFile("/401.html");
        String expectBody = new String(Files.readAllBytes(errorPage.toPath()));

        assertThat(result.statusCode()).isEqualTo(401);
        assertThat(result.body()).isEqualTo(expectBody);
    }

    @Test
    void 요청_URI가_login이고_POST_메서드일때_로그인에_필요한_파라미터가_없으면_404를_반환하고_로그인페이지로_이동한다() throws IOException {
        String body = "account=notExist";
        final var result = TestHttpUtils.sendPost("http://127.0.0.1:8080", "/login", body);

        File responsePage = ResourceParser.getRequestFile("/login.html");
        String expectBody = new String(Files.readAllBytes(responsePage.toPath()));

        assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.getStatusCode());
        assertThat(result.body()).isEqualTo(expectBody);
    }
}
