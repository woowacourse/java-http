package com.techcourse.servlet.mapping;

import org.apache.coyote.http11.request.HttpServletRequest;

public interface HandlerMapping {

    Object getHandler(HttpServletRequest httpServletRequest);
}
