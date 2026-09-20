package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

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
}
