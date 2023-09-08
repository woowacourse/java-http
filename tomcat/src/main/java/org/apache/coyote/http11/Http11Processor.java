package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpUtils.bufferingInputStream;
import static org.apache.coyote.http11.HttpUtils.getContentType;
import static org.apache.coyote.http11.HttpUtils.readContentsFromFile;

import java.io.IOException;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.responseline.HttpStatus;
import org.apache.coyote.http11.responseline.ResponseLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

  private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);


  private static final String HTTP_1_1 = "HTTP/1.1";

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
      final HttpResponse response;

      final String url = request.getUrl();
      response = handlingResponse(url, request);

      outputStream.write(response.build().getBytes());
      outputStream.flush();
    } catch (final IOException | UncheckedServletException e) {
      log.error(e.getMessage(), e);
    }
  }

  private HttpResponse handlingResponse(final String url, final HttpRequest request)
      throws IOException {
    if (url.equals("/login")) {
      final LoginController loginController = new LoginController();
      return loginController.service(request);
    } else if (url.equals("/register")) {
      final RegisterController registerController = new RegisterController();
      return registerController.service(request);
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
}
