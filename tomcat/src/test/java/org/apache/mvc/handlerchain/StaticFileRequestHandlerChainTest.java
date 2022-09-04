package org.apache.mvc.handlerchain;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.Test;

class StaticFileRequestHandlerChainTest {

    @Test
    void handle() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080",
                "Connection: keep-alive ",
                "",
                "");

        StaticFileRequestHandlerChain chain = new StaticFileRequestHandlerChain(null);
        // when
        HttpResponse response = chain.handle(HttpRequest.parse(new ByteArrayInputStream(httpRequest.getBytes())), HttpResponse.initial());

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        List<String> expected = List.of("HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 5564",
                "",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath())));

        assertThat(response.getAsString()).contains(expected);
    }
}
