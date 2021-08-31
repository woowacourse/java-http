package nextstep.jwp.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IndexControllerTest extends ControllerTest {

    @DisplayName("GET / 요청 시 'Hello world!' 메시지를 응답한다.")
    @Test
    void doGet() {
        // when
        sendRequest();

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }
}
