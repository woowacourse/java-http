package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

  private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
  private static final Map<String, String> extender = Map.of(
      "js", "text/javascript",
      "html", "text/html",
      "css", "text/css"
  );
  private static final String REGEX = "\\.(css|html|js)$";
  private static final Pattern PATTERN = Pattern.compile(REGEX);

  private final Socket connection;

  public Http11Processor(final Socket connection) {
    this.connection = connection;
  }

  @Override
  public void run() {
    log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
    process(connection);
  }

  @Override
  public void process(final Socket connection) {
    try (final OutputStream outputStream = connection.getOutputStream();
        final BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {

      List<String> result = new ArrayList<>();
      String line;
      while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
        result.add(line);
      }

      final String http = result.get(0);
      StringTokenizer stringTokenizer = new StringTokenizer(http, " ");
      String response = null;
      String responseBody = null;

      final String httpMethod = stringTokenizer.nextToken();
      final String uri = stringTokenizer.nextToken();

      if (httpMethod.equals("GET") && uri.equals("/")) {
        response = handleWelcomePage(uri);
      }

      if (httpMethod.equals("GET") && uri.startsWith("/login")) {
        final int index = uri.indexOf("?");

        if (index > 0) {
          final String path = uri.substring(0, index);
          final String queryString = uri.substring(index + 1);

          if (path.equals("/login")) {

            stringTokenizer = new StringTokenizer(queryString, "=&");

            final String accountKey = stringTokenizer.nextToken();
            final String accountValue = stringTokenizer.nextToken();
            final String passwordKey = stringTokenizer.nextToken();
            final String passwordValue = stringTokenizer.nextToken();

            final Optional<User> account = InMemoryUserRepository.findByAccount(accountValue);

            final User user = account.get();

            System.out.println(user.getAccount());

            final URL resource = getClass().getClassLoader().getResource("static/login.html");
            responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
          }
        }
      }

      final Set<String> extenders = extender.keySet();

      if (extenders.contains(extractExtension(uri))) {
        response = makeStaticResponse(uri);
      }

      outputStream.write(response.getBytes());
      outputStream.flush();
    } catch (IOException |
             UncheckedServletException e) {
      log.error(e.getMessage(), e);
    }
  }

  private String makeStaticResponse(final String uri) throws IOException {
    final URL resource = getClass()
        .getClassLoader()
        .getResource("static" + uri);

    final String responseBody = new String(
        Files.readAllBytes(new File(resource.getFile()).toPath()));

    return String.join("\r\n",
        "HTTP/1.1 200 OK ",
        "Content-Type: " + extender.get(extractExtension(uri)) + ";charset=utf-8 ",
        "Content-Length: " + responseBody.getBytes().length + " ",
        "",
        responseBody);
  }

  private String handleWelcomePage(final String uri) {
    final String responseBody = "Hello world!";

    return String.join("\r\n",
        "HTTP/1.1 200 OK ",
        "Content-Type: text/html;charset=utf-8 ",
        "Content-Length: " + responseBody.getBytes().length + " ",
        "",
        responseBody);
  }

  private String extractExtension(final String uri) {
    final Matcher matcher = PATTERN.matcher(uri);

    if (matcher.find()) {
      return matcher.group(1);
    }

    return "";
  }
}
