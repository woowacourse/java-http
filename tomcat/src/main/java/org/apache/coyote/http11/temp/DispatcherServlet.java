package org.apache.coyote.http11.temp;

import org.apache.coyote.http11.Servlet;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class DispatcherServlet implements Servlet {

    private final RequestMapping requestMapping;

    public DispatcherServlet(final RequestMapping requestMapping) {
        this.requestMapping = requestMapping;
    }

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) throws Exception {
        Handler handler = requestMapping.getHandler(httpRequest);
        handler.handle(httpRequest, httpResponse);
    }
}
