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
import java.util.UUID;
import java.util.stream.Collectors;
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
      final String requestBody = extractBody(requestHeaders.get("Content-Length"), bufferedReader);
      final String response = handleRequest(startLine, requestHeaders, requestBody);

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

  private String extractBody(String contentLength, BufferedReader bufferedReader)
      throws IOException {
    if (contentLength == null) {
      return null;
    }
    int length = Integer.parseInt(contentLength);
    char[] buffer = new char[length];
    bufferedReader.read(buffer, 0, length);
    return new String(buffer);
  }

  private String handleRequest(final String startLine, final Map<String, String> headers,
      final String requestBody)
      throws URISyntaxException, IOException {
    String setCookie = null;
    final HttpCookie cookie = new HttpCookie(headers.get("Cookie"));
    int statusCode = 200;
    String statusMessage = "OK";
    String responseBody = "";
    String contentType = "text/html";
    String location = null;

    URL filePathUrl = null;
    final List<String> startLineTokens = List.of(startLine.split(" "));
    String method = startLineTokens.get(0);
    // ===== uri => path, queryString
    String uri = startLineTokens.get(1);
    int uriSeparatorIndex = uri.indexOf("?");
    String path;
    Map<String, String> queryProperties = new HashMap<>();
    if (uriSeparatorIndex == -1) {
      path = uri;
    } else {
      path = uri.substring(0, uriSeparatorIndex);
      final String queryString = uri.substring(uriSeparatorIndex + 1);
      // 쿼리스트링 파싱
      String[] queryTokens = queryString.split("&");
      for (int i = 0; i < queryTokens.length; i++) {
        int equalSeperatorIndex = queryTokens[i].indexOf("=");
        if (equalSeperatorIndex != -1) {
          queryProperties.put(queryTokens[i].substring(0, equalSeperatorIndex),
              queryTokens[i].substring(equalSeperatorIndex + 1));

        }
      }
    }

    if (method.equals("POST")) {
      if (path.equals("/register")) {
        if (requestBody != null) {
          // 파싱
          Map<String, String> parsedRequestBody = new HashMap<>();
          String[] queryTokens = requestBody.split("&");
          for (int i = 0; i < queryTokens.length; i++) {
            int equalSeperatorIndex = queryTokens[i].indexOf("=");
            if (equalSeperatorIndex != -1) {
              parsedRequestBody.put(queryTokens[i].substring(0, equalSeperatorIndex),
                  queryTokens[i].substring(equalSeperatorIndex + 1));

            }
          }
          InMemoryUserRepository.save(new User(
              Long.getLong(parsedRequestBody.get("id")),
              parsedRequestBody.get("account"),
              parsedRequestBody.get("password"),
              parsedRequestBody.get("email")
          ));
          statusCode = 201;
          statusMessage = "Found";
          filePathUrl = getClass().getResource("/static/index.html");
        }
      } else if (path.equals("/login")) {
        if (requestBody != null) {
          Map<String, String> parsedRequestBody = new HashMap<>();
          String[] queryTokens = requestBody.split("&");
          for (int i = 0; i < queryTokens.length; i++) {
            int equalSeperatorIndex = queryTokens[i].indexOf("=");
            if (equalSeperatorIndex != -1) {
              parsedRequestBody.put(queryTokens[i].substring(0, equalSeperatorIndex),
                  queryTokens[i].substring(equalSeperatorIndex + 1));
            }
          }
          if (parsedRequestBody.get("account").equals("gugu")  // 로그인 성공
              && parsedRequestBody.get("password").equals("password")) {
            if (cookie.isExist("JSESSIOINID")) {
              setCookie = "JSESSIONID=" + UUID.randomUUID();
            }
            statusCode = 302;
            statusMessage = "Found";
            location = "/index.html";
          } else {
            statusCode = 401;
            statusMessage = "Unauthorization";
            filePathUrl = getClass().getResource("/static/401.html");
          }
        }
      }
    } else if (method.equals("GET")) {
      if (path.equals("/login")) {
        filePathUrl = getClass().getResource("/static/login.html");
      } else if (path.equals("/register")) {
        filePathUrl = getClass().getResource("/static/register.html");
      } else {  // 핸들러(컨트롤러)가 없을 때
        filePathUrl = Optional.ofNullable(getClass().getResource("/static" + path))
            .orElse(getClass().getResource("/static/404.html"));
      }
    }
    if (filePathUrl != null) {
      final Path filePath = Paths.get(Objects.requireNonNull(filePathUrl).toURI());
      Charset charset = StandardCharsets.UTF_8;
      responseBody = String.join(System.lineSeparator(), Files.readAllLines(filePath, charset));
      if (isRequestCssFile(headers, startLineTokens.get(1).split("."))) {
        contentType = "text/css";
      }
    }

    //response header 생성
    Map<String, String> responseHeaders = new HashMap<>();
    responseHeaders.put("Content-Type", contentType + ";charset=utf-8");
    responseHeaders.put("Content-Length", String.valueOf(responseBody.getBytes().length));
    if (location != null) {
      responseHeaders.put("Location", location);
    }
    if (setCookie != null) {
      responseHeaders.put("Set-Cookie", setCookie);
    }
    final String responseHeader = String.join(" \r\n",
        responseHeaders.entrySet()
            .stream()
            .map(entry -> entry.getKey() + ": " + entry.getValue()
            ).collect(Collectors.toList()));

    return String.join("\r\n",
        "HTTP/1.1 " + statusCode + " " + statusMessage + " ",
        responseHeader,
        "",
        responseBody);
  }

  private boolean isRequestCssFile(final Map<String, String> headers, final String[] tokens) {
    return (tokens.length >= 1 && tokens[tokens.length - 1].equals("css")) || headers.get("Accept")
        .contains("text/css");
  }
}
