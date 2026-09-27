package org.apache.coyote.http11;

import static com.techcourse.Application.createRequestHandlerResolver;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.catalina.connector.CoyoteAdapter;
import org.apache.catalina.handle.RequestHandler;
import org.apache.catalina.handle.RequestHandlerResolver;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpResponse;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorBadRequestTest {

    public Processor createHttp11Processor(Socket socket) {
        return createHttp11Processor(socket, createRequestHandlerResolver());
    }

    public Processor createHttp11Processor(Socket socket, RequestHandlerResolver requestHandlerResolver) {
        return new Http11Processor(socket, new CoyoteAdapter(requestHandlerResolver));
    }

    @Test
    void invalidContentLengthReturnsBadRequest() {
        for (String contentLength : List.of("abc", "", "-1", "+1", "1.5", "2147483648")) {
            final var socket = new StubSocket(requestWithContentLength(contentLength));
            final var processor = createHttp11Processor(socket);

            processor.process(socket);

            assertThat(socket.output())
                    .as("Content-Length: %s", contentLength)
                    .startsWith("HTTP/1.1 400 Bad Request \r\n")
                    .contains("Connection: close \r\n")
                    .endsWith("\r\n\r\nBad Request");
        }
    }

    @Test
    void badRequestIsSentBeforeConnectionCloses() throws IOException {
        final var loopback = InetAddress.getLoopbackAddress();
        try (var listener = new ServerSocket(0, 1, loopback);
             var client = new Socket(loopback, listener.getLocalPort());
             var server = listener.accept()) {
            client.setSoTimeout(3000);
            server.setSoTimeout(3000);
            client.getOutputStream().write(requestWithContentLength("abc").getBytes(StandardCharsets.UTF_8));
            client.getOutputStream().flush();

            createHttp11Processor(server).process(server);

            final String response = new String(client.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            assertThat(response)
                    .startsWith("HTTP/1.1 400 Bad Request \r\n")
                    .contains("Connection: close \r\n", "Content-Length: 11 \r\n")
                    .endsWith("\r\n\r\nBad Request");
        }
    }

    @Test
    void handlerNumberFormatExceptionIsNotClassifiedAsBadRequest() {
        final var socket = new StubSocket();
        final var failure = new NumberFormatException("server-side conversion failed");
        final var handler = new RequestHandler() {
            @Override
            public void handle(HttpRequest request, HttpResponse response) {
                throw failure;
            }

            @Override
            public boolean canHandle(HttpRequest request) {
                return true;
            }
        };

        final var requestHandlerResolver = new RequestHandlerResolver();
        requestHandlerResolver.registerLast(handler);

        final var processor = createHttp11Processor(socket,requestHandlerResolver);

        assertThatThrownBy(() -> processor.process(socket)).isSameAs(failure);
        assertThat(socket.output()).isEmpty();
    }

    private String requestWithContentLength(String contentLength) {
        return String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost",
                "Content-Length: " + contentLength,
                "", "");
    }
}
