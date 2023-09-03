package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

  private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
  private static final String PREFIX_STATIC_PATH = "tomcat/src/main/resources/static";
  private static final String NEW_LINE = "\r\n";

  private final Socket connection;

  public Http11Processor(final Socket connection) {
    this.connection = connection;
  }

  @Override
  public void run() {
    log.info("connect host: {}, port: {}", this.connection.getInetAddress(),
        this.connection.getPort());
    process(this.connection);
  }

  @Override
  public void process(final Socket connection) {
    try (final var inputStream = bufferingInputStream(connection.getInputStream());
        final var outputStream = connection.getOutputStream()
    ) {
      final String request = inputStream.readLine();
      final String url = request.split(" ")[1];

      final String responseBody = readContentsFromFile(url);

      final var response = String.join(NEW_LINE,
          "HTTP/1.1 200 OK ",
          "Content-Type: text/html;charset=utf-8 ",
          "Content-Length: " + responseBody.getBytes().length + " ",
          "",
          responseBody);

      outputStream.write(response.getBytes());
      outputStream.flush();
    } catch (final IOException | UncheckedServletException e) {
      log.error(e.getMessage(), e);
    }
  }

  private BufferedReader bufferingInputStream(final InputStream inputStream) {
    final InputStreamReader reader = new InputStreamReader(inputStream);
    return new BufferedReader(reader);
  }

  private String readContentsFromFile(final String url) throws IOException {
    final Path path = Paths.get(PREFIX_STATIC_PATH + url);
    final List<String> contents = Files.readAllLines(path);

    return String.join(NEW_LINE, contents);
  }
}
