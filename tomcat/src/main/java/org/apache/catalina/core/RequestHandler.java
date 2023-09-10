package org.apache.catalina.core;

import org.apache.catalina.core.servlet.HttpServletResponse;
import org.apache.coyote.http11.request.Request;

public interface RequestHandler {

    void service(Request request, HttpServletResponse response) throws Exception;

}
