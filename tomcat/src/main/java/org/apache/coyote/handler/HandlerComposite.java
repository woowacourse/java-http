package org.apache.coyote.handler;

import java.util.List;
import java.util.Optional;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class HandlerComposite implements Handler {

  private final List<Handler> handlers;

  public HandlerComposite(final List<Handler> handlers) {
    this.handlers = handlers;
  }

  @Override
  public boolean canHandle(final HttpRequest httpRequest) {
    return handlers.stream()
        .anyMatch(it -> it.canHandle(httpRequest));
  }

  @Override
  public void handle(final HttpRequest httpRequest, final HttpResponse httpResponse) {
    final Optional<Handler> handler = handlers.stream()
        .filter(it -> it.canHandle(httpRequest))
        .findAny();

    handler.ifPresentOrElse(
        it -> it.safeHandle(httpRequest, httpResponse),
        () -> {
          throw new IllegalArgumentException("handler가 존재하지 않습니다.");
        }
    );
  }
}
