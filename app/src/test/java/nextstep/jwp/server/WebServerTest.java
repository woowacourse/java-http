package nextstep.jwp.server;

import nextstep.jwp.MockSocket;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebServerTest {

    @InjectMocks
    private WebServer webServer;

    @Mock
    private ServerSocket serverSocket;

    @DisplayName("웹 서버를 실행한다.")
    @Test
    void run() throws IOException {
        MockSocket mockSocket = mock(MockSocket.class);
        given(serverSocket.accept()).willReturn(mockSocket);

        webServer.run();

        verify(serverSocket, times(1)).accept();
        verify(mockSocket, times(1)).isConnected();
    }

    @ParameterizedTest
    @MethodSource("argumentsForDefaultPortIfNull")
    void defaultPortIfNull(String[] args, int expected) {
        assertThat(WebServer.defaultPortIfNull(args)).isEqualTo(expected);
    }

    private static Stream<Arguments> argumentsForDefaultPortIfNull() {
        return Stream.of(
                Arguments.of(new String[] {}, 8080),
                Arguments.of(new String[] {"8000"}, 8000),
                Arguments.of(new String[] {"9000"}, 9000)
        );
    }
}
