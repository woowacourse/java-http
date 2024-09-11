package com.techcourse.servlet;

import jakarta.servlet.http.HttpServletRequest;

public interface HandlerMapping {

    Object getHandler(HttpServletRequest httpServletRequest);
}
