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
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.domain.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

  private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
  private static final ClassLoader CLASS_LOADER = ClassLoader.getSystemClassLoader();
  private static final String PREFIX_STATIC_PATH = "static";
  private static final String INDEX_PAGE = "/index.html";
  private static final String LOGIN_PAGE = "/login.html";
  private static final String JSESSIONID = "JSESSIONID";

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
      final HttpRequest request = HttpRequest.from(inputStream);

      final SessionManager sessionManager = new SessionManager();
      final String response;

      if (request.getUrl().equals("/login")) {
        final String sessionId = request.getCookie(JSESSIONID);
        response = login(sessionManager, sessionId, request);
      } else if (request.getUrl().equals("/register")) {
        final int contentLength = Integer.parseInt(request.getHeader("Content-Length"));
        final Map<String, String> body = readBody(inputStream, contentLength);
        response = register(body);
      } else {
        final String responseBody = readContentsFromFile(request.getUrl());
        final String contentType = getContentType(request);
        response = response200(contentType, responseBody);
      }

      outputStream.write(response.getBytes());
      outputStream.flush();
    } catch (final IOException | UncheckedServletException e) {
      log.error(e.getMessage(), e);
    }
  }

  private String login(
      final SessionManager sessionManager,
      final String sessionId,
      final HttpRequest request
  ) {
    final String account = request.getParam("account");
    final String password = request.getParam("password");
    if (isAuthorized(sessionId, sessionManager)) {
      return response302(INDEX_PAGE);
    } else if (account == null) {
      return response302(LOGIN_PAGE);
    }
    return authorize(account, password, sessionManager);
  }

  private boolean isAuthorized(final String sessionId, final SessionManager sessionManager) {
    return sessionId != null && sessionManager.findSession(sessionId) != null;
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
    return response302(INDEX_PAGE);
  }

  private String authorize(
      final String account,
      final String password,
      final SessionManager sessionManager
  ) {
    final Optional<User> user = InMemoryUserRepository.findByAccount(account);

    if (user.isPresent() && user.get().checkPassword(password)) {
      final String uuid = UUID.randomUUID().toString();
      final Session session = new Session(uuid);
      sessionManager.add(session);
      return response302(INDEX_PAGE, "JSESSIONID=" + uuid);
    }
    return response302("/401.html");
  }

  private String response200(final String contentType, final String responseBody) {
    return String.join(System.lineSeparator(),
        "HTTP/1.1 200 OK ",
        "Content-Type: " + contentType + ";charset=utf-8 ",
        "Content-Length: " + responseBody.getBytes().length + " ",
        "",
        responseBody);
  }

  private String response302(final String location) {
    return String.join(System.lineSeparator(),
        "HTTP/1.1 302 FOUND ",
        "Location: " + location,
        "");
  }

  private String response302(final String location, final String cookie) {
    return String.join(System.lineSeparator(),
        "HTTP/1.1 302 FOUND ",
        "Location: " + location,
        "Set-Cookie: " + cookie,
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

  private String getContentType(final HttpRequest request) {
    final String accept = request.getHeader("Accept");
    if (accept == null) {
      return "text/html";
    }
    return accept.split(",")[0];
  }

  private boolean isInvalidFile(final URL resource) {
    return resource == null || new File(resource.getFile()).isDirectory();
  }
}
