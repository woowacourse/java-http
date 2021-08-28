package nextstep.jwp;

import nextstep.jwp.core.handler.FrontHandler;
import nextstep.mockweb.request.MockRequest;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {

    @Test
    void run() {
        // when
        final String result =
                MockRequest.get("/").logAll()
                        .result().asString();

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final FrontHandler frontHandler = new FrontHandler("nextstep");
        final RequestHandler requestHandler = new RequestHandler(socket, frontHandler);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String text = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        System.out.println(text.length());
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n"+
                text;
        assertThat(socket.output()).isEqualTo(expected);
    }
}
