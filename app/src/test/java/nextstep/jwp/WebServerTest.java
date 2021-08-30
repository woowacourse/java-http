package nextstep.jwp;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WebServerTest {

    @Test
    void defaultPortIfNull() {
        //given
        String[] args = {};
        //when
        final int port = WebServer.defaultPortIfNull(args);
        //then
        assertThat(port).isEqualTo(8080);
    }

    @Test
    void choosePort() {
        //given
        String[] args = {"8081"};
        //when
        final int port = WebServer.defaultPortIfNull(args);
        //then
        assertThat(port).isEqualTo(8081);
    }

    @Test
    void defaultPortConstructor() {
        //given
        String[] args = {"65536"};
        final int port = WebServer.defaultPortIfNull(args);
        //when
        final WebServer webServer = new WebServer(port);
        //then
        assertThat(webServer.getPort()).isEqualTo(8080);
    }
}