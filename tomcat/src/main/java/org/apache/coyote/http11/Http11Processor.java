package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
import org.apache.coyote.http11.request.HttpRequest;
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
        final var bufferedReader = bufferingInputStream(connection.getInputStream());
        final var outputStream = connection.getOutputStream()
    ) {
      final HttpRequest request = HttpRequest.from(bufferedReader);
      final SessionManager sessionManager = new SessionManager();
      final String response;

      final String url = request.getUrl();
      if (url.equals("/login")) {
        final String sessionId = request.getCookie(JSESSIONID);
        final String account = request.getParam("account");
        final String password = request.getParam("password");
        response = login(sessionManager, sessionId, account, password);
      } else if (url.equals("/register")) {
        final Map<String, String> bodyParams = extractParams(request.getBody());
        response = register(bodyParams);
      } else {
        final String responseBody = readContentsFromFile(url);
        final String contentType = getContentType(request.getHeader("Accept"));
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
      final String account,
      final String password
  ) {
    if (isAuthorized(sessionId, sessionManager)) {
      return response302(INDEX_PAGE);
    } else if (account == null || password == null) {
      return response302(LOGIN_PAGE);
    }
    return authorize(account, password, sessionManager);
  }

  private boolean isAuthorized(final String sessionId, final SessionManager sessionManager) {
    return sessionId != null && sessionManager.findSession(sessionId) != null;
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

    for (final String query : URLDecoder.decode(queryString, StandardCharsets.UTF_8).split("&")) {
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

  private boolean isInvalidFile(final URL resource) {
    return resource == null || new File(resource.getFile()).isDirectory();
  }

  private String getContentType(final String accept) {
    if (accept == null) {
      return "text/html";
    }
    return accept.split(",")[0];
  }
}
