package org.apache.coyote.handler;

import java.io.IOException;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public interface Handler {

  boolean canHandle(final HttpRequest httpRequest);

  void handle(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException;

  default void safeHandle(final HttpRequest httpRequest, final HttpResponse httpResponse) {
    try {
      handle(httpRequest, httpResponse);
    } catch (IOException e) {
      throw new IllegalArgumentException("I/O 작업 관련 에러가 발생했습니다.");
    }
  }
}
