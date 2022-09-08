package nextstep.jwp.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class ResourceControllerTest {

    @Test
    void css() throws IOException {
        //given
        final String httpRequest= String.join("\r\n",
            "GET /css/styles.css HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Accept: text/css,*/*;q=0.1 ",
            "Connection: keep-alive",
            "",
            "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expected = String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/css;charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void javascript() throws IOException {
        //given
        final String httpRequest= String.join("\r\n",
            "GET /js/scripts.js HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Accept: text/css,*/*;q=0.1 ",
            "Connection: keep-alive",
            "",
            "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/js/scripts.js");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expected = String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/javascript;charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);

        assertThat(socket.output()).isEqualTo(expected);
    }
}
