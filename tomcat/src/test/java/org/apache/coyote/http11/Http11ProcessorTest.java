package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static support.ResponseAssertions.assertHtml;
import static support.ResponseAssertions.resourceBytes;

import com.techcourse.controller.StaticResourceController;
import com.techcourse.view.ResourceRenderer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    void process() {
        var socket = new StubSocket();
        var processor = new Http11Processor(socket,
                new RequestMapping(new StaticResourceController(new ResourceRenderer())));

        processor.process(socket);

        assertHtml(socket.output(), "Hello world!".getBytes(StandardCharsets.UTF_8));
        assertSessionCookie(socket.output());
    }

    @Test
    void index() throws IOException {
        var socket = new StubSocket("GET /index.html HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");
        var processor = new Http11Processor(socket,
                new RequestMapping(new StaticResourceController(new ResourceRenderer())));

        processor.process(socket);

        assertHtml(socket.output(), resourceBytes("/index.html"));
        assertSessionCookie(socket.output());
    }

    private void assertSessionCookie(String response) {
        String headers = response.split("\r\n\r\n", 2)[0];
        var cookies = Arrays.stream(headers.split("\r\n"))
                .filter(line -> line.startsWith("Set-Cookie: JSESSIONID="))
                .toList();
        assertThat(cookies).hasSize(1);
        String sessionId = cookies.getFirst().substring("Set-Cookie: JSESSIONID=".length());
        try {
            assertThat(UUID.fromString(sessionId).toString()).isEqualTo(sessionId);
            assertThat(SessionManager.getInstance().findSession(sessionId)).isNotNull();
        } finally {
            SessionManager.getInstance().remove(sessionId);
        }
    }
}
