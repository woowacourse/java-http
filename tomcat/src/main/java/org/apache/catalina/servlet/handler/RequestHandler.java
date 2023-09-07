package org.apache.catalina.servlet.handler;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public interface RequestHandler {

    Response service(Request request);

    boolean canHandle(Request request);

}
