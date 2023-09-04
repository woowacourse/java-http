package org.apache.coyote.handler;

import java.io.IOException;
import org.apache.coyote.request.HttpRequest;

public interface Handler {

  boolean canHandle(final HttpRequest httpRequest);

  String handle(final HttpRequest httpRequest) throws IOException;

  default String safeHandle(final HttpRequest httpRequest) {
    try {
      return handle(httpRequest);
    } catch (IOException e) {
      throw new IllegalArgumentException("I/O 작업 관련 에러가 발생했습니다.");
    }
  }
}
