package org.apache.catalina.core.servlet;

import static org.apache.catalina.core.servlet.HttpServletResponse.methodNotAllowed;
import static org.apache.coyote.http11.common.Method.GET;
import static org.apache.coyote.http11.common.Method.POST;
import static org.apache.coyote.http11.common.Status.METHOD_NOT_ALLOWED;

import org.apache.catalina.core.RequestHandler;

public abstract class HttpServlet implements RequestHandler {

    private final String mappingPath;

    protected HttpServlet(final String mappingPath) {
        this.mappingPath = mappingPath;
    }

    @Override
    public void service(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
            throws Exception {
        if (httpServletRequest.getMethod() == GET) {
            doGet(httpServletRequest, httpServletResponse);
            return;
        }
        if (httpServletRequest.getMethod() == POST) {
            doPost(httpServletRequest, httpServletResponse);
            return;
        }
        httpServletResponse.set(methodNotAllowed());
    }

    public String getMappingPath() {
        return mappingPath;
    }

    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        response.setStatus(METHOD_NOT_ALLOWED);
    }

    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) {
        response.setStatus(METHOD_NOT_ALLOWED);
    }

}
