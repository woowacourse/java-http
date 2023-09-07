package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpUtils.bufferingInputStream;
import static org.apache.coyote.http11.HttpUtils.getContentType;
import static org.apache.coyote.http11.HttpUtils.readContentsFromFile;

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
import org.apache.coyote.http11.headers.HttpHeader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.responseline.HttpStatus;
import org.apache.coyote.http11.responseline.ResponseLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

  private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
  private static final ClassLoader CLASS_LOADER = ClassLoader.getSystemClassLoader();
  private static final String PREFIX_STATIC_PATH = "static";
  private static final String INDEX_PAGE = "/index.html";
  private static final String LOGIN_PAGE = "/login.html";
  private static final String HTTP_1_1 = "HTTP/1.1";
  private static final String JSESSIONID = "JSESSIONID";
  private static final String LOCATION = "Location";
  private static final String HTML_401 = "/401.html";

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
      final HttpResponse response;

      final String url = request.getUrl();
      response = handlingResponse(url, request, sessionManager);

      outputStream.write(response.build().getBytes());
      outputStream.flush();
    } catch (final IOException | UncheckedServletException e) {
      log.error(e.getMessage(), e);
    }
  }

  private HttpResponse handlingResponse(final String url, final HttpRequest request,
      final SessionManager sessionManager) throws IOException {
    if (url.equals("/login")) {
      final String sessionId = request.getCookie(JSESSIONID);
      final String account = request.getParam("account");
      final String password = request.getParam("password");
      return login(sessionManager, sessionId, account, password);
    } else if (url.equals("/register")) {
      final Map<String, String> bodyParams = extractParams(request.getBody());
      return register(bodyParams);
    } else {
      final String contentType = getContentType(request.getHeader("Accept"));
      return responseStaticFile(url, contentType);
    }
  }

  private HttpResponse responseStaticFile(final String url, final String contentType)
      throws IOException {
    final String body = readContentsFromFile(url);
    final ResponseLine responseLine = new ResponseLine(HTTP_1_1, HttpStatus.OK);
    final HttpHeader header = new HttpHeader();
    header.setHeader("Content-Type", contentType + ";charset=utf-8");
    header.setHeader("Content-Length", body.getBytes().length + "");
    return new HttpResponse(responseLine, header, body);
  }

  private HttpResponse login(
      final SessionManager sessionManager,
      final String sessionId,
      final String account,
      final String password
  ) {
    if (isAuthorized(sessionId, sessionManager)) {
      final ResponseLine responseLine = new ResponseLine(HTTP_1_1, HttpStatus.FOUND);
      final HttpHeader header = new HttpHeader();
      header.setHeader(LOCATION, INDEX_PAGE);
      return new HttpResponse(responseLine, header);
    } else if (account == null || password == null) {
      final ResponseLine responseLine = new ResponseLine(HTTP_1_1, HttpStatus.FOUND);
      final HttpHeader header = new HttpHeader();
      header.setHeader(LOCATION, LOGIN_PAGE);
      return new HttpResponse(responseLine, header);
    }
    return authorize(account, password, sessionManager);
  }

  private HttpResponse authorize(
      final String account,
      final String password,
      final SessionManager sessionManager
  ) {
    final Optional<User> user = InMemoryUserRepository.findByAccount(account);

    if (user.isPresent() && user.get().checkPassword(password)) {
      final String uuid = UUID.randomUUID().toString();
      final Session session = new Session(uuid);
      sessionManager.add(session);

      final ResponseLine responseLine = new ResponseLine(HTTP_1_1, HttpStatus.FOUND);
      final HttpHeader header = new HttpHeader();
      header.setHeader(LOCATION, INDEX_PAGE);
      return new HttpResponse(responseLine, header);
    }
    final ResponseLine responseLine = new ResponseLine(HTTP_1_1, HttpStatus.UNAUTHORIZED);
    final HttpHeader header = new HttpHeader();
    header.setHeader(LOCATION, HTML_401);
    return new HttpResponse(responseLine, header);
  }

  private boolean isAuthorized(final String sessionId, final SessionManager sessionManager) {
    return sessionId != null && sessionManager.findSession(sessionId) != null;
  }

  private HttpResponse register(final Map<String, String> body) {
    final String account = body.get("account");
    final String password = body.get("password");
    final String email = body.get("email");
    final User user = new User(account, password, email);
    InMemoryUserRepository.save(user);

    final ResponseLine responseLine = new ResponseLine(HTTP_1_1, HttpStatus.FOUND);
    final HttpHeader header = new HttpHeader();
    header.setHeader(LOCATION, INDEX_PAGE);
    return new HttpResponse(responseLine, header);
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
