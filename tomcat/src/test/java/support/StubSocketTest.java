package support;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class StubSocketTest {

    @Test
    void inputStreamKeepsItsPositionAcrossCalls() throws IOException {
        final var socket = new StubSocket("GET / HTTP/1.1\r\n\r\n");
        final var inputStream = socket.getInputStream();

        assertThat(inputStream.read()).isEqualTo('G');
        assertThat(socket.getInputStream()).isSameAs(inputStream);
        assertThat(socket.getInputStream().read()).isEqualTo('E');
    }
}
