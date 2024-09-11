package com.techcourse.servlet.mapping;

import org.apache.coyote.http11.request.HttpServletRequest;
import org.apache.coyote.http11.response.HttpServletResponse;

public interface Servlet {

    void doService(HttpServletRequest servletRequest, HttpServletResponse servletResponse);
}
