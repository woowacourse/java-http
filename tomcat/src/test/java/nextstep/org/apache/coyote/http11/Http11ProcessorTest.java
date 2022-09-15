package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.ExtensionContentType;
import org.apache.coyote.http11.HeaderElement;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.http11response.StatusCode;
import org.junit.jupiter.api.Test;
import support.ResponseParser;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        ResponseParser responseParser = ResponseParser.of(socket.output());

        assertThat(responseParser.getStatusCode()).isEqualTo(Integer.toString(StatusCode.OK.getCode()));
        assertThat(responseParser.getHeaderElements()).containsEntry(HeaderElement.CONTENT_TYPE.getValue(),
                ExtensionContentType.HTML.getContentType());
        assertThat(responseParser.getHeaderElements()).containsEntry(HeaderElement.CONTENT_LENGTH.getValue(), "12");
        assertThat(responseParser.getHeaderElements()).containsKey(HeaderElement.SET_COOKIE.getValue());
        assertThat(responseParser.getBody()).isEqualTo("Hello world!");
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

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);
        ResponseParser responseParser = ResponseParser.of(socket.output());

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        File file = new File(resource.getFile());
        assertThat(responseParser.getStatusCode()).isEqualTo(Integer.toString(StatusCode.OK.getCode()));
        assertThat(responseParser.getHeaderElements()).containsEntry(HeaderElement.CONTENT_TYPE.getValue(),
                ExtensionContentType.HTML.getContentType());
        assertThat(responseParser.getHeaderElements()).containsEntry(HeaderElement.CONTENT_LENGTH.getValue(), Long.toString(file.length()));
        assertThat(responseParser.getHeaderElements()).containsKey(HeaderElement.SET_COOKIE.getValue());
        assertThat(responseParser.getBody()).isEqualTo(new String(Files.readAllBytes(file.toPath())));
    }

    @Test
    void login_Get() throws IOException {
        final String httpRequest= String.join("\r\n",
                "GET /login.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);
        ResponseParser responseParser = ResponseParser.of(socket.output());

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        File file = new File(resource.getFile());
        assertThat(responseParser.getStatusCode()).isEqualTo(Integer.toString(StatusCode.OK.getCode()));
        assertThat(responseParser.getHeaderElements()).containsEntry(HeaderElement.CONTENT_TYPE.getValue(),
                ExtensionContentType.HTML.getContentType());
        assertThat(responseParser.getHeaderElements()).containsEntry(HeaderElement.CONTENT_LENGTH.getValue(), Long.toString(file.length()));
        assertThat(responseParser.getHeaderElements()).containsKey(HeaderElement.SET_COOKIE.getValue());
        assertThat(responseParser.getBody()).isEqualTo(new String(Files.readAllBytes(file.toPath())));
    }

    @Test
    void login_Post_Success() throws IOException {
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30 ",
                "",
                "account=gugu&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);
        ResponseParser responseParser = ResponseParser.of(socket.output());

        assertThat(responseParser.getStatusCode()).isEqualTo(Integer.toString(StatusCode.FOUND.getCode()));
        assertThat(responseParser.getHeaderElements()).containsEntry(HeaderElement.LOCATION.getValue(), "/index.html");
    }

    @Test
    void login_Post_Fail() throws IOException {
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 26 ",
                "",
                "account=gugu&password=fail");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);
        ResponseParser responseParser = ResponseParser.of(socket.output());

        assertThat(responseParser.getStatusCode()).isEqualTo(Integer.toString(StatusCode.FOUND.getCode()));
        assertThat(responseParser.getHeaderElements()).containsEntry(HeaderElement.LOCATION.getValue(), "/401.html");
    }
}
