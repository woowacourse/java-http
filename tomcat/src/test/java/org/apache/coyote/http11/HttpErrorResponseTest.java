package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HttpErrorResponseTest {

    @Test
    void malformedRequestsReceiveBadRequest() {
        String[] requests = {
                "GET\r\n\r\n",
                "GET / HTTP/1.1\r\nBroken-Header\r\n\r\n",
                "POST /login HTTP/1.1\r\nContent-Length: abc\r\n\r\n",
                "POST /login HTTP/1.1\r\nContent-Length: -1\r\n\r\n",
                "POST /login HTTP/1.1\r\nContent-Length: 3\r\n\r\na",
                "GET / HTTP/1.1\r\nHost: localhost\r\n"
        };
        for (String request : requests) {
            StubSocket socket = new StubSocket(request);
            new Http11Processor(socket, new SessionManager()).process(socket);

            assertError(socket.output(), "400 Bad Request", "Bad Request");
        }
    }

    @Test
    void unsupportedMethodsReceiveAllowedMethodsForTheResource() {
        for (String method : new String[]{"PUT", "DELETE", "POST"}) {
            StubSocket socket = new StubSocket(method + " / HTTP/1.1\r\n\r\n");
            new Http11Processor(socket, new SessionManager()).process(socket);

            assertError(socket.output(), "405 Method Not Allowed", "Method Not Allowed");
            assertThat(socket.output()).contains("\r\nAllow: GET\r\n");
        }
        StubSocket socket = new StubSocket("PUT /login HTTP/1.1\r\n\r\n");
        new Http11Processor(socket, new SessionManager()).process(socket);

        assertError(socket.output(), "405 Method Not Allowed", "Method Not Allowed");
        assertThat(socket.output()).contains("\r\nAllow: GET, POST\r\n");
    }

    @Test
    void controllerFailureReceivesGenericServerError() {
        SessionManager sessions = mock(SessionManager.class);
        when(sessions.isSessionContainsKey("existing", "user"))
                .thenThrow(new IllegalArgumentException("internal details"));
        StubSocket socket = new StubSocket("GET /login HTTP/1.1\r\nCookie: JSESSIONID=existing\r\n\r\n");

        new Http11Processor(socket, sessions).process(socket);

        assertError(socket.output(), "500 Internal Server Error", "Internal Server Error");
        assertThat(socket.output()).doesNotContain("internal details", "Set-Cookie:");
    }

    @Test
    void fileReadFailureReceivesServerError() {
        // css is a resource directory, so it cannot be read as a text file.
        StubSocket socket = new StubSocket("GET /css HTTP/1.1\r\n\r\n");

        new Http11Processor(socket, new SessionManager()).process(socket);

        assertError(socket.output(), "500 Internal Server Error", "Internal Server Error");
    }

    @Test
    void errorIsWrittenBeforeOutputStreamCloses() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        boolean[] closed = {false};
        StubSocket socket = new StubSocket("GET\r\n\r\n") {
            @Override
            public OutputStream getOutputStream() {
                return new OutputStream() {
                    @Override
                    public void write(int value) throws IOException {
                        if (closed[0]) {
                            throw new IOException("Stream closed");
                        }
                        bytes.write(value);
                    }

                    @Override
                    public void close() {
                        closed[0] = true;
                    }
                };
            }
        };

        new Http11Processor(socket, new SessionManager()).process(socket);

        assertError(bytes.toString(StandardCharsets.UTF_8), "400 Bad Request", "Bad Request");
        assertThat(closed[0]).isTrue();
    }

    @Test
    void doesNotRetryResponseWhenWritingFails() {
        int[] writes = {0};
        StubSocket socket = new StubSocket() {
            @Override
            public OutputStream getOutputStream() {
                return new OutputStream() {
                    @Override
                    public void write(int value) throws IOException {
                        writes[0]++;
                        throw new IOException("Connection lost");
                    }
                };
            }
        };

        new Http11Processor(socket, new SessionManager()).process(socket);

        assertThat(writes[0]).isEqualTo(1);
    }

    private void assertError(String response, String status, String body) {
        String[] parts = response.split("\r\n\r\n", 2);
        assertThat(parts).hasSize(2);
        String[] headers = parts[0].split("\r\n");
        assertThat(headers[0]).isEqualTo("HTTP/1.1 " + status);
        assertThat(headers).contains(
                "Content-Type: text/plain;charset=UTF-8",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length
        );
        assertThat(parts[1]).isEqualTo(body);
    }
}
