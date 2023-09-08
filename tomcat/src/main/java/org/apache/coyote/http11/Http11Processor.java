package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpUtils.bufferingInputStream;
import static org.apache.coyote.http11.HttpUtils.readContentsFromFile;

import java.io.IOException;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.RequestHandler;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.responseline.HttpStatus;
import org.apache.coyote.http11.responseline.ResponseLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

  private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
  private static final RequestHandler REQUEST_HANDLER = new RequestHandler();
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

      final HttpResponse response = handlingResponse(request);

      outputStream.write(response.build().getBytes());
      outputStream.flush();
    } catch (final Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private HttpResponse handlingResponse(final HttpRequest request)
      throws Exception {
    return REQUEST_HANDLER.handle(request);
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
