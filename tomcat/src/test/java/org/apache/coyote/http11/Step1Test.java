package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.catalina.Manager;
import org.apache.catalina.SessionManager;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Step1Test {

    @Test
    void 루트_요청에_Hello_world를_응답한다() {
        StubSocket socket = new StubSocket();

        process(socket);

        assertThat(socket.output())
                .endsWith("\r\n\r\nHello world!");
    }

    @Test
    void index_html_요청에_인덱스_페이지를_응답한다()
            throws IOException {
        String request = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "",
                ""
        );
        StubSocket socket = new StubSocket(request);

        process(socket);

        URL resource = getClass()
                .getClassLoader()
                .getResource("static/index.html");

        String expected = Files.readString(
                new File(resource.getFile()).toPath()
        );

        assertThat(socket.output())
                .endsWith("\r\n\r\n" + expected);
    }

    @Test
    void CSS_요청에_파일과_CSS_Content_Type을_응답한다()
            throws IOException {
        String request = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1",
                "Host: localhost:8080",
                "",
                ""
        );
        StubSocket socket = new StubSocket(request);

        process(socket);

        URL resource = getClass()
                .getClassLoader()
                .getResource("static/css/styles.css");

        String expected = Files.readString(
                new File(resource.getFile()).toPath()
        );

        assertThat(socket.output())
                .contains("Content-Type: text/css;charset=utf-8");

        assertThat(socket.output())
                .endsWith("\r\n\r\n" + expected);
    }

    @Test
    void Query_String이_있는_로그인_요청에_로그인_페이지를_응답한다()
            throws IOException {
        String request = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1",
                "Host: localhost:8080",
                "",
                ""
        );
        StubSocket socket = new StubSocket(request);

        process(socket);

        URL resource = getClass()
                .getClassLoader()
                .getResource("static/login.html");

        String expected = Files.readString(
                new File(resource.getFile()).toPath()
        );

        assertThat(socket.output())
                .endsWith("\r\n\r\n" + expected);
    }

    private void process(final StubSocket socket) {
        Manager manager = new SessionManager();

        new Http11Processor(socket, manager, new RequestMapping(new StaticResourceController()))
                .process(socket);
    }
}
