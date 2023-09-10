package org.apache.catalina.core;

import org.apache.catalina.core.servlet.HttpServletRequest;
import org.apache.catalina.core.servlet.HttpServletResponse;

public interface RequestHandler {

    void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception;

}
