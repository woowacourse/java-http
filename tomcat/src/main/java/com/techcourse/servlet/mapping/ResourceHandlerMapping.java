package com.techcourse.servlet.mapping;

import com.techcourse.servlet.StaticResourceServlet;
import org.apache.coyote.http11.HttpProtocol;
import org.apache.coyote.http11.Servlet;
import org.apache.coyote.http11.request.HttpServletRequest;
import org.apache.coyote.http11.request.line.Method;

public class ResourceHandlerMapping implements HandlerMapping {

    private static final String STATIC_RESOURCE_PATH = "static";
    private static final Method SUPPORTING_METHOD = Method.GET;
    private static final HttpProtocol SUPPORTING_PROTOCOL = HttpProtocol.HTTP_11;
    private final StaticResourceServlet staticResourceServlet = new StaticResourceServlet();


    @Override
    public boolean hasHandlerFor(HttpServletRequest httpServletRequest) {
        return isAvailableResourceRequest(httpServletRequest);
    }

    @Override
    public Servlet getHandler(HttpServletRequest request) {
        return staticResourceServlet;
    }

    private boolean isAvailableResourceRequest(HttpServletRequest request) {
        return request.methodEquals(SUPPORTING_METHOD) &&
                request.protocolEquals(SUPPORTING_PROTOCOL) &&
                !request.isUriHome() &&
                resourceAvailable(request.getUriPath());
    }

    private boolean resourceAvailable(String fileName) {
        try {
            String filePath = getClass().getClassLoader().getResource(STATIC_RESOURCE_PATH + fileName).getFile();
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }
}
