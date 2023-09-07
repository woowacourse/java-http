package org.apache.catalina.servlet.handler;

import static org.apache.coyote.http11.common.Method.GET;
import static org.apache.coyote.http11.common.Method.POST;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public abstract class AbstractHandler implements RequestHandler {

    @Override
    public Response service(final Request request) {
        if (request.getMethod() == GET) {
            return doGet(request);
        }
        if (request.getMethod() == POST) {
            return doPost(request);
        }
        return Response.methodNotAllowed()
                .build();
    }

    protected Response doPost(final Request request) {
        return Response.methodNotAllowed()
                .build();
    }

    protected Response doGet(final Request request) {
        return Response.methodNotAllowed()
                .build();
    }
}
