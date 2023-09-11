package org.apache.coyote.http11.handler;

import java.util.HashSet;
import java.util.Set;
import org.apache.coyote.http11.handler.mapper.HandlerMapper;
import org.apache.coyote.http11.handler.mapper.IndexHandlerMapper;
import org.apache.coyote.http11.handler.mapper.LoginHandlerMapper;
import org.apache.coyote.http11.handler.mapper.RegisterHandlerMapper;
import org.apache.coyote.http11.handler.mapper.StaticFileHandlerMapper;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.responseline.HttpStatus;

public class RequestHandler {

  private static final Set<HandlerMapper> mappers = new HashSet<>();

  static {
    mappers.add(new LoginHandlerMapper());
    mappers.add(new RegisterHandlerMapper());
    mappers.add(new IndexHandlerMapper());
    mappers.add(new StaticFileHandlerMapper());
  }

  private RequestHandler() {
  }

  public static void handle(final HttpRequest request, final HttpResponse response) {
    for (final HandlerMapper mapper : mappers) {
      if (mapper.isSupport(request)) {
        mapper.handle(request, response);
        return;
      }
    }
    response.setBodyAsStaticFile("/404.html");
    response.setStatus(HttpStatus.NOT_FOUND);
  }
}
