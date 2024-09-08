package org.apache.coyote.mapper;

import org.apache.coyote.request.HttpRequest;
import org.apache.handler.Handler;

public interface HandlerMapping {

    Handler getHandler(HttpRequest request);
}
