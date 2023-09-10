package org.apache.catalina.core;

import org.apache.catalina.core.servlet.ServletResponse;
import org.apache.coyote.http11.request.Request;

public interface RequestHandler {

    void service(Request request, ServletResponse response) throws Exception;

}
