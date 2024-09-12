package com.techcourse.servlet.mapping;

import org.apache.coyote.http11.Servlet;
import org.apache.coyote.http11.request.HttpServletRequest;

public interface HandlerMapping {

    boolean hasHandlerFor(HttpServletRequest httpServletRequest);

    Servlet getHandler(HttpServletRequest httpServletRequest);
}
