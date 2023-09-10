package org.apache.catalina.servlet.handler;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response.ServletResponse;

public interface RequestHandler {

    void service(Request request, ServletResponse response) throws Exception;

}
