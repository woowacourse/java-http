package org.apache.coyote.http11.handler.mapper;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface HandlerMapper {

  boolean isSupport(HttpRequest request);

  HttpResponse handle(HttpRequest request) throws Exception;
}
