package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

  @Test
  void process() {
    // given
    final var socket = new StubSocket();
    final var processor = new Http11Processor(socket);

    // when
    processor.process(socket);

    // then
    final var expected = String.join(System.lineSeparator(),
        "HTTP/1.1 200 OK",
        "Content-Length: 12",
        "Content-Type: text/html;charset=utf-8",
        "",
        "Hello world!");

    assertThat(socket.output()).isEqualTo(expected);
  }

  @Test
  void index() throws IOException {
    // given
    final String httpRequest = String.join(System.lineSeparator(),
        "GET /index.html HTTP/1.1 ",
        "Host: localhost:8080 ",
        "Connection: keep-alive ",
        "",
        "");

    final var socket = new StubSocket(httpRequest);
    final Http11Processor processor = new Http11Processor(socket);

    // when
    processor.process(socket);

    // then
    final URL resource = getClass().getClassLoader().getResource("static/index.html");
    final var expected = "HTTP/1.1 200 OK" + System.lineSeparator() +
        "Content-Length: 4439" + System.lineSeparator() +
        "Content-Type: text/html;charset=utf-8" + System.lineSeparator() +
        System.lineSeparator() +
        new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

    assertThat(socket.output()).isEqualTo(expected);
  }
}
