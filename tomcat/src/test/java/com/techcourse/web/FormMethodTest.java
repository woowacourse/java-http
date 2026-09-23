package com.techcourse.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class FormMethodTest {

    @Test
    void 로그인_form은_POST로_전송한다() throws IOException {
        // given
        String resourceName = "login.html";

        // when
        String loginHtml = readStaticHtml(resourceName);

        // then
        assertThat(loginHtml).contains("<form method=\"post\" action=\"login\">");
    }

    @Test
    void 회원가입_form은_POST로_전송한다() throws IOException {
        // given
        String resourceName = "register.html";

        // when
        String registerHtml = readStaticHtml(resourceName);

        // then
        assertThat(registerHtml).contains("<form method=\"post\" action=\"register\">");
    }

    private String readStaticHtml(String name) throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream("/static/" + name)) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
