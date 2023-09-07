package org.apache.coyote.handler;

import java.util.List;
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
    handlers.stream()
        .filter(it -> it.canHandle(httpRequest))
        .forEach(it -> it.safeHandle(httpRequest, httpResponse));
  }
}
