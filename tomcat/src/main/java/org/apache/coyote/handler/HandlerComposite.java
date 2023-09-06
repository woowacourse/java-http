package org.apache.coyote.handler;

import java.util.List;
import org.apache.coyote.request.HttpRequest;

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
  public String handle(final HttpRequest httpRequest) {
    return handlers.stream()
        .filter(it -> it.canHandle(httpRequest))
        .map(it -> it.safeHandle(httpRequest))
        .findAny()
        .orElseThrow(() -> new IllegalArgumentException("handler가 존재하지 않습니다."));
  }
}
