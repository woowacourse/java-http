package org.apache.catalina.core;

import org.apache.catalina.core.servlet.HttpServletRequest;
import org.apache.catalina.core.servlet.HttpServletResponse;

public class RequestHandlerAdaptor {

    private final RequestMapping handlerMapping;

    public RequestHandlerAdaptor(final RequestMapping requestMapping) {
        handlerMapping = requestMapping;
    }

    public void service(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
            throws Exception {
        final var handler = handlerMapping.getHandler(httpServletRequest);

        handler.service(httpServletRequest, httpServletResponse);
    }

}
