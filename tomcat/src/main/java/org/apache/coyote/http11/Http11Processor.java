package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

  private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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
    try (final var inputStream = connection.getInputStream();
        final var outputStream = connection.getOutputStream()) {
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      final String startLine = extractStartLine(bufferedReader);
      if (startLine == null) {
        return;
      }
      final Map<String, String> requestHeaders = extractHeader(bufferedReader);

      final String response = handleRequest(startLine, requestHeaders);

      outputStream.write(response.getBytes());
      outputStream.flush();
    } catch (IOException | UncheckedServletException | URISyntaxException e) {
      log.error(e.getMessage(), e);
    }
  }

  private String extractStartLine(final BufferedReader bufferedReader) throws IOException {
    return bufferedReader.readLine();
  }

  private Map<String, String> extractHeader(final BufferedReader bufferedReader)
      throws IOException {
    Map<String, String> headers = new HashMap<>();
    String line = bufferedReader.readLine();
    while (!"".equals(line)) {
      String[] tokens = line.split(": ");
      headers.put(tokens[0], tokens[1]);
      line = bufferedReader.readLine();
    }
    return headers;
  }

  private String handleRequest(final String startLine, final Map<String, String> headers)
      throws URISyntaxException, IOException {
    String responseBody = "";
    String contentType = "text/html";
    final List<String> startLineTokens = List.of(startLine.split(" "));
    if (startLineTokens.get(0).equals("GET")) {
      responseBody = handleGetRequest(startLineTokens);
      if (isRequestCssFile(headers, startLineTokens.get(1).split("."))) {
        contentType = "text/css";
      }
    }

    return String.join("\r\n",
        "HTTP/1.1 200 OK ",
        "Content-Type: " + contentType + ";charset=utf-8 ",
        "Content-Length: " + responseBody.getBytes().length + " ",
        "",
        responseBody);
  }

  private boolean isRequestCssFile(final Map<String, String> headers, final String[] tokens) {
    return (tokens.length >= 1 && tokens[tokens.length - 1].equals("css")) || headers.get("Accept")
        .contains("text/css");
  }

  private String handleGetRequest(final List<String> tokens)
      throws URISyntaxException, IOException {
    String uri = tokens.get(1);
    int uriSeperatorIndex = uri.indexOf("?");
    String path;
    String queryString;
    if (uriSeperatorIndex == -1) {
      path = uri;
      queryString = "";
    } else {
      path = uri.substring(0, uriSeperatorIndex);
      queryString = uri.substring(uriSeperatorIndex + 1);
    }
    URL filePathUrl;
    if (path.equals("/login")) {
      filePathUrl = getClass().getResource("/static/login.html");
      if (!queryString.equals("")) {
        Map<String, String> queryProperties = new HashMap<>();
        String[] queryTokens = queryString.split("&");
        for (int i = 0; i < queryTokens.length; i++) {
          String[] queryPair = queryTokens[i].split("=");
          queryProperties.put(queryPair[0], queryPair[1]);
        }
        final Optional<User> user = InMemoryUserRepository.findByAccount(
            queryProperties.get("account"));
        if (user.isPresent()) {
          log.info("회원 정보 : {}", user);
        }
      }
    } else {
      filePathUrl = Optional.ofNullable(getClass().getResource("/static" + path))
          .orElse(getClass().getResource("/static/404.html"));
    }

    final Path filePath = Paths.get(Objects.requireNonNull(filePathUrl).toURI());
    Charset charset = StandardCharsets.UTF_8;
    return String.join(System.lineSeparator(), Files.readAllLines(filePath, charset));
  }
}
