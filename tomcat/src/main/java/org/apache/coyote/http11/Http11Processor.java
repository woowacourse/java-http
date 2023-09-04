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
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.domain.Request;
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
      final Request request = new Request(inputStream.readLine());
      final Map<String, String> headers = extractHeaders(inputStream);
      final Map<String, String> params = extractParams(request.getQueryString());

      final String response;

      if (request.getUrl().equals("/login")) {
        final String account = params.get("account");
        response = login(account);
      } else if (request.getUrl().equals("/register")) {
        final int contentLength = Integer.parseInt(headers.get("Content-Length"));
        final Map<String, String> body = readBody(inputStream, contentLength);
        response = register(body);
      } else {
        final String responseBody = readContentsFromFile(request.getUrl());
        final String contentType = getContentType(headers);
        response = response200(contentType, responseBody);
      }

      outputStream.write(response.getBytes());
      outputStream.flush();
    } catch (final IOException | UncheckedServletException e) {
      log.error(e.getMessage(), e);
    }
  }

  private Map<String, String> readBody(
      final BufferedReader inputStream,
      final int contentLength
  ) throws IOException {
    final char[] buffer = new char[contentLength];
    inputStream.read(buffer);
    return extractParams(new String(buffer));
  }

  private String register(final Map<String, String> body) {
    final User user = new User(body.get("account"), body.get("password"), body.get("email"));
    InMemoryUserRepository.save(user);
    return response302("/index.html");
  }

  private String login(final String account) {
    final Optional<User> user = InMemoryUserRepository.findByAccount(account);

    if (user.isPresent()) {
      log.debug(user.toString());
      return response302("/index.html");
    }
    return response302("/401.html");
  }

  private String response200(final String contentType, final String responseBody) {
    return String.join(NEW_LINE,
        "HTTP/1.1 200 OK ",
        "Content-Type: " + contentType + ";charset=utf-8 ",
        "Content-Length: " + responseBody.getBytes().length + " ",
        "",
        responseBody);
  }

  private String response302(final String location) {
    return String.join(NEW_LINE,
        "HTTP/1.1 302 FOUND ",
        "Location: " + location,
        "");
  }

  private Map<String, String> extractParams(final String queryString) {
    final Map<String, String> params = new HashMap<>();
    if (queryString.isEmpty()) {
      return params;
    }

    for (final String query : queryString.split("&")) {
      final String[] tokens = query.split("=");
      params.put(tokens[0], tokens[1]);
    }
    return params;
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
