package org.apache.catalina.container;

import com.techcourse.Application;
import jakarta.http.HttpSessionWrapper;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ContainerTest {

    @Test
    void process() {
        // given
        StubSocket socket = new StubSocket();
        Container container = new Container(Application.requestMapping(), mock(HttpSessionWrapper.class));
        Runnable runnable = container.acceptConnection(socket);

        // when
        runnable.run();

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        // given
        StubSocket socket = new StubSocket(httpRequest);
        Container container = new Container(Application.requestMapping(), mock(HttpSessionWrapper.class));
        Runnable runnable = container.acceptConnection(socket);

        // when
        runnable.run();

        // then
        URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
                          "Content-Type: text/html;charset=utf-8 \r\n" +
                          "Content-Length: 5564 \r\n" +
                          "\r\n" +
                          new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }
}
