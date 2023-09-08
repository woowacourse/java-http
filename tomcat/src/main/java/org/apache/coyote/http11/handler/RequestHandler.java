package org.apache.coyote.http11.handler;

import java.util.HashSet;
import java.util.Set;
import org.apache.coyote.http11.handler.mapper.HandlerMapper;
import org.apache.coyote.http11.handler.mapper.LoginHandlerMapper;
import org.apache.coyote.http11.handler.mapper.RegisterHandlerMapper;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RequestHandler {

  private static final Set<HandlerMapper> mappers = new HashSet<>();

  static {
    mappers.add(new LoginHandlerMapper());
    mappers.add(new RegisterHandlerMapper());
  }

  public HttpResponse handle(final HttpRequest request) throws Exception {
    for (final HandlerMapper mapper : mappers) {
      if (mapper.isSupport(request)) {
        return mapper.handle(request);
      }
    }
    return null;
  }
}
