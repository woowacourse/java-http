package nextstep.jwp;

import nextstep.jwp.http.controller.standard.StandardControllerFactory;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {

    @Test
    void run() {
//        // given
//        final MockSocket socket = new MockSocket();
//        final RequestHandler requestHandler =
//            new RequestHandler(socket, StandardControllerFactory.create());
//
//        // when
//        requestHandler.run();
//
//        // then
//        String expected = String.join("\r\n",
//                "HTTP/1.1 200 OK ",
//                "Content-Type: text/html;charset=utf-8 ",
//                "Content-Length: 12 ",
//                "",
//                "Hello world!");
//        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /test.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler =
            new RequestHandler(socket, StandardControllerFactory.create());

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/test.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        System.out.println(Files.readAllBytes(new File(resource.getFile()).toPath()).length);
        //assertThat(socket.output()).isEqualTo(expected);
    }
}
