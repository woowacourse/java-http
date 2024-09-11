package com.techcourse.servlet.mapping;

import org.apache.coyote.http11.HttpRequestHandler;
import org.apache.coyote.http11.request.HttpServletRequest;

public interface HandlerMapping {

    boolean hasHandlerFor(HttpServletRequest httpServletRequest);

    HttpRequestHandler getHandler(HttpServletRequest httpServletRequest);
}
