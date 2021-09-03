package nextstep.jwp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FrontControllerTest {

    @DisplayName("요청에 쿠키가 없으면 응답에 set-cookie를 심어보낸다.")
    @Test
    void run() {
        // given
        final MockSocket socket = new MockSocket();
        final FrontController frontController = new FrontController(socket);

        // when
        frontController.run();

        // then
        String expectedContains = "Set-Cookie: JSESSIONID=";
        assertThat(socket.output()).contains(expectedContains);
    }
}
