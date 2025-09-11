package org.apache.catalina;

import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public interface RequestHandler {

    boolean canHandle(Http11Request request);

    void handle(Http11Request request, Http11Response response);
}
