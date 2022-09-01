package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import nextstep.jwp.exception.UncheckedServletException;
import org.junit.jupiter.api.Test;

class HttpRequestStartLineTest {

    @Test
    void 입력_문자열이_blank면_예외를_던진다() {
        // given
        final var input = " ";

        // when & then
        assertThatThrownBy(() -> RequestStartLine.from(input))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("Http Request Start Line이 비어있습니다.");
    }

    @Test
    void 입력_문자열이_형식에_맞지_않을_경우_예외를_던진다() {
        // given
        final var input = "GET/index.html HTTP/1.1 ";

        // when & then
        assertThatThrownBy(() -> RequestStartLine.from(input))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("Http Request Start Line이 형식에 맞지 않습니다.");
    }

    @Test
    void 메시지에서_body를_파싱한다() throws IOException {
        // given
        final var message = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 58 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=gugu&password=password&email=hkkang%40woowahan.com"
        );
        final var inputStream = new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));

        // when
        final var body = HttpRequest.from(inputStream).getBody();

        // then
        assertThat(body).isEqualTo("account=gugu&password=password&email=hkkang%40woowahan.com");
    }
}
