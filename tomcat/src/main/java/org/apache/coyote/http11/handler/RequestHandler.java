package org.apache.coyote.http11.handler;

import java.util.HashSet;
import java.util.Set;
import org.apache.coyote.http11.handler.mapper.HandlerMapper;
import org.apache.coyote.http11.handler.mapper.LoginHandlerMapper;
import org.apache.coyote.http11.handler.mapper.RegisterHandlerMapper;
import org.apache.coyote.http11.handler.mapper.StaticFileHandlerMapper;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.responseline.HttpStatus;
import org.apache.coyote.http11.responseline.ResponseLine;

public class RequestHandler {

  private static final Set<HandlerMapper> mappers = new HashSet<>();

  static {
    mappers.add(new LoginHandlerMapper());
    mappers.add(new RegisterHandlerMapper());
    mappers.add(new StaticFileHandlerMapper());
  }

  private static HttpResponse responseHelloWorld() {
    final ResponseLine responseLine = new ResponseLine(HttpStatus.OK);
    final HttpHeader header = new HttpHeader();
    header.setHeader("Content-Length", "12");
    header.setHeader("Content-Type", "text/html;charset=utf-8");
    return new HttpResponse(responseLine, header, "Hello world!");
  }

  public HttpResponse handle(final HttpRequest request) throws Exception {
    for (final HandlerMapper mapper : mappers) {
      if (mapper.isSupport(request)) {
        return mapper.handle(request);
      }
    }
    return responseHelloWorld();
  }
}
