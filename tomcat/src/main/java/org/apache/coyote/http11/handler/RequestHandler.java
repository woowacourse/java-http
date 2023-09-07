package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public interface RequestHandler {

    Response service(Request request);

    boolean canHandle(Request request);

}
