package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

  private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
  private static final ClassLoader CLASS_LOADER = ClassLoader.getSystemClassLoader();
  private static final String PREFIX_STATIC_PATH = "static";
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
    try (
        final var inputStream = bufferingInputStream(connection.getInputStream());
        final var outputStream = connection.getOutputStream()
    ) {
      final String request = inputStream.readLine();
      final String url = request.split(" ")[1];
      final Map<String, String> headers = extractHeaders(inputStream);

      final String responseBody = readContentsFromFile(url);
      final String contentType = getContentType(headers);

      final var response = String.join(NEW_LINE,
          "HTTP/1.1 200 OK ",
          "Content-Type: " + contentType + ";charset=utf-8 ",
          "Content-Length: " + responseBody.getBytes().length + " ",
          "",
          responseBody);

      outputStream.write(response.getBytes());
      outputStream.flush();
    } catch (final IOException | UncheckedServletException e) {
      log.error(e.getMessage(), e);
    }
  }

  private Map<String, String> extractHeaders(final BufferedReader inputStream) throws IOException {
    final Map<String, String> headers = new HashMap<>();
    String line;
    while (!(line = inputStream.readLine()).isEmpty()) {
      final String[] tokens = line.split(": ");
      headers.put(tokens[0].trim(), tokens[1].trim());
    }
    return headers;
  }

  private BufferedReader bufferingInputStream(final InputStream inputStream) {
    final InputStreamReader reader = new InputStreamReader(inputStream);
    return new BufferedReader(reader);
  }

  private String readContentsFromFile(final String url) throws IOException {
    final URL resource = CLASS_LOADER.getResource(PREFIX_STATIC_PATH + url);
    if (isInvalidFile(resource)) {
      return "Hello world!";
    }

    final File file = new File(resource.getFile());
    return new String(Files.readAllBytes(file.toPath()));
  }

  private String getContentType(final Map<String, String> headers) {
    final String accept = headers.get("Accept");
    if (accept == null) {
      return "text/html";
    }
    return accept.split(",")[0];
  }

  private boolean isInvalidFile(final URL resource) {
    return resource == null || new File(resource.getFile()).isDirectory();
  }
}
