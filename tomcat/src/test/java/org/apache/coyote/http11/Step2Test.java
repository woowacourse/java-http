package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import support.StubSocket;

class Step2Test {

    @Test
    void 로그인에_성공하면_302_상태_코드와_index_html_Location으로_응답한다() {
        // given
        String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        var socket = new StubSocket(httpRequest);
        var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).startsWith("HTTP/1.1 302");
        assertThat(socket.output()).contains("Location: /index.html");
    }

    @ParameterizedTest
    @CsvSource({
            "unknown, password",
            "gugu, wrong"
    })
    void 로그인에_실패하면_302_상태_코드와_401_html_Location으로_응답한다(
            String account,
            String password
    ) {
        // given
        String httpRequest = String.join("\r\n",
                "GET /login?account=" + account + "&password=" + password + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        var socket = new StubSocket(httpRequest);
        var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).startsWith("HTTP/1.1 302");
        assertThat(socket.output()).contains("Location: /401.html");
    }

    @Test
    void 회원가입_하면_index_html로_리다이렉트한다() {
        // given
        String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 56 ",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                "");

        var socket = new StubSocket(httpRequest);
        var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).startsWith("HTTP/1.1 302");
        assertThat(socket.output()).contains("Location: /index.html");
    }

    @Test
    void 로그인_페이지에서_로그인_버튼을_누르면_POST_요청을_보낸다() {
        // given
        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");
        var socket = new StubSocket(httpRequest);
        var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("<form method=\"post\" action=\"login\">");
    }

    @Test
    void JSESSIONID가_없는_요청에는_새_쿠키를_설정한다() {
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry",
                "",
                "");
        var socket = new StubSocket(httpRequest);

        new Http11Processor(socket).process(socket);

        String cookieHeader = socket.output().lines()
                .filter(line -> line.startsWith("Set-Cookie: JSESSIONID="))
                .findFirst()
                .orElseThrow();
        String sessionId = cookieHeader.substring("Set-Cookie: JSESSIONID=".length());
        assertThat(UUID.fromString(sessionId).toString()).isEqualTo(sessionId);
    }

    @Test
    void JSESSIONID가_있는_요청에는_새_쿠키를_설정하지_않는다() {
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: yummy_cookie=choco; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
                "",
                "");
        var socket = new StubSocket(httpRequest);

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).doesNotContain("Set-Cookie: JSESSIONID=");
    }
}
