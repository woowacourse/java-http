package support;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("테스트용 소켓")
class StubSocketTest {

    @Test
    @DisplayName("입력 스트림을 다시 가져오면 같은 객체를 반환한다")
    void returnsTheSameInputStreamAcrossCalls() {
        // given
        final var socket = new StubSocket();
        final var inputStream = socket.getInputStream();

        // when
        final var retrievedInputStream = socket.getInputStream();

        // then
        assertThat(retrievedInputStream).isSameAs(inputStream);
    }

    @Test
    @DisplayName("입력 스트림을 다시 가져와도 읽던 위치가 유지된다")
    void inputStreamKeepsItsPositionAcrossCalls() throws IOException {
        // given
        final var socket = new StubSocket("GET / HTTP/1.1\r\n\r\n");
        socket.getInputStream().read();

        // when
        final var secondByte = socket.getInputStream().read();

        // then
        assertThat(secondByte).isEqualTo('E');
    }
}
