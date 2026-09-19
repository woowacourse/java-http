package support;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("테스트용 소켓")
class StubSocketTest {

    @Test
    @DisplayName("입력 스트림을 다시 가져와도 읽던 위치가 유지된다")
    void inputStreamKeepsItsPositionAcrossCalls() throws IOException {
        // given
        final var socket = new StubSocket("GET / HTTP/1.1\r\n\r\n");
        final var inputStream = socket.getInputStream();

        // when
        final var firstByte = inputStream.read();
        final var retrievedInputStream = socket.getInputStream();
        final var secondByte = retrievedInputStream.read();

        // then
        assertThat(firstByte).isEqualTo('G');
        assertThat(retrievedInputStream).isSameAs(inputStream);
        assertThat(secondByte).isEqualTo('E');
    }
}
