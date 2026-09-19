package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorLoginTest {

    @Test
    void GET_방식으로_로그인_페이지를_조회한다() {
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");
        final var socket = new StubSocket(httpRequest);

        new Http11Processor(socket).process(socket);

        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK")
                .contains("<form method=\"post\" action=\"login\">");
    }

    @Test
    void POST_로그인에_성공하면_index로_리다이렉트한다() {
        final StubSocket socket = postLoginRequest("gugu", "password");

        new Http11Processor(socket).process(socket);

        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found")
                .contains("Location: /index.html");
    }

    @Test
    void 비밀번호가_일치하지_않으면_401로_리다이렉트한다() {
        final StubSocket socket = postLoginRequest("gugu", "wrong");

        new Http11Processor(socket).process(socket);

        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found")
                .contains("Location: /401.html");
    }

    @Test
    void 사용자가_존재하지_않으면_401로_리다이렉트한다() {
        final StubSocket socket = postLoginRequest("unknown", "password");

        new Http11Processor(socket).process(socket);

        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found")
                .contains("Location: /401.html");
    }

    private StubSocket postLoginRequest(final String account, final String password) {
        final String requestBody = "account=" + account + "&password=" + password;
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + requestBody.getBytes(StandardCharsets.UTF_8).length,
                "",
                requestBody);
        return new StubSocket(httpRequest);
    }
}
