package org.apache.catalina.servlet.handler;

import static org.apache.catalina.servlet.handler.ServletResponse.methodNotAllowed;
import static org.apache.coyote.http11.common.Method.GET;
import static org.apache.coyote.http11.common.Method.POST;
import static org.apache.coyote.http11.common.Status.METHOD_NOT_ALLOWED;

import org.apache.coyote.http11.request.Request;

public abstract class Servlet implements RequestHandler {

    private final String mappingPath;

    protected Servlet(final String mappingPath) {
        this.mappingPath = mappingPath;
    }

    @Override
    public void service(final Request request, final ServletResponse response) throws Exception {
        if (request.getMethod() == GET) {
            doGet(request, response);
            return;
        }
        if (request.getMethod() == POST) {
            doPost(request, response);
            return;
        }
        methodNotAllowed().build();
    }

    public String getMappingPath() {
        return mappingPath;
    }

    protected void doGet(final Request request, final ServletResponse response) throws Exception {
        response.setStatus(METHOD_NOT_ALLOWED);
    }

    protected void doPost(final Request request, final ServletResponse response) {
        response.setStatus(METHOD_NOT_ALLOWED);
    }

}
