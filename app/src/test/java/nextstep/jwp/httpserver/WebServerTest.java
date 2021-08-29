package nextstep.jwp.httpserver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("webserver 테스트")
class WebServerTest {

    @Test
    @DisplayName("port 번호 결정 - port값을 지정한 경우")
    void selectPort() {
        // given
        String[] args = {"12345"};

        // when
        int port = WebServer.defaultPortIfNull(args);

        // then
        assertThat(port).isEqualTo(12345);
    }

    @Test
    @DisplayName("port 번호 결정 - port값을 지정하지 않은 경우")
    void defaultPortIfNull() {
        // given
        String[] args = {};

        // when
        int port = WebServer.defaultPortIfNull(args);

        // then
        assertThat(port).isEqualTo(8080);
    }
}
