package org.apache.coyote.http11;


import static org.apache.coyote.http11.utils.IOUtils.bufferingInputStream;

import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.RequestHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
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
      final HttpResponse response = RequestHandler.handle(request);

      outputStream.write(response.build().getBytes());
      outputStream.flush();
    } catch (final Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
