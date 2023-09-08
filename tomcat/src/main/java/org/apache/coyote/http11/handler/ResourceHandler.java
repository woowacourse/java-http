package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.response.HttpStatusCode.OK;

import java.io.IOException;
import org.apache.catalina.SessionManger;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ResourceHandler extends RequestHandler {

    public ResourceHandler(final SessionManger sessionManager) {
        super(sessionManager);
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        try {
            return getResource(httpRequest, OK);
        } catch (NullPointerException e) {
            return getNotFoundPage(httpRequest);
        }
    }
}
