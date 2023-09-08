package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.catalina.Manager;
import org.apache.coyote.Processor;
import org.apache.coyote.handler.Handler;
import org.apache.coyote.handler.HandlerComposite;
import org.apache.coyote.parser.HttpRequestReader;
import org.apache.coyote.request.Cookie;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.HttpRequestLine;
import org.apache.coyote.request.QueryString;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.request.SessionManager;
import org.apache.coyote.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

  private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
  private static final Manager SESSION_MANAGER = new SessionManager();

  private final Socket connection;
  private final HandlerComposite handlerComposite;

  public Http11Processor(final Socket connection, final List<Handler> handlers) {
    this.connection = connection;
    this.handlerComposite = new HandlerComposite(handlers);
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
            new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))
    ) {

      List<String> result = new ArrayList<>();
      String line;
      while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
        result.add(line);
      }

      final HttpRequestLine httpRequestLine = HttpRequestReader.parseHttpRequestLine(result);
      final RequestBody requestBody = HttpRequestReader.parseRequestBody(result, bufferedReader);
      final QueryString queryString = HttpRequestReader.parseQueryString(result);
      final Cookie cookie = HttpRequestReader.parseCookie(result);

      final HttpRequest httpRequest = new HttpRequest(
          httpRequestLine,
          queryString,
          requestBody,
          cookie,
          SESSION_MANAGER
      );

      final HttpResponse httpResponse = new HttpResponse();

      handlerComposite.safeHandle(httpRequest, httpResponse);

      outputStream.write(httpResponse.read().getBytes());
      outputStream.flush();
    } catch (IOException | UncheckedServletException e) {
      log.error(e.getMessage(), e);
    }
  }
}
