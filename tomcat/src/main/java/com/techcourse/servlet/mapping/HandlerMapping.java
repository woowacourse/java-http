package com.techcourse.servlet.mapping;

import org.apache.catalina.servlet.Servlet;
import org.apache.coyote.http11.request.HttpServletRequest;

public interface HandlerMapping {

    boolean hasHandlerFor(HttpServletRequest httpServletRequest);

    Servlet getHandler(HttpServletRequest httpServletRequest);
}
