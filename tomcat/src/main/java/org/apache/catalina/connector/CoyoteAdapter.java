package org.apache.catalina.connector;

import org.apache.catalina.controller.RequestMapping;
import org.apache.coyote.Adapter;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class CoyoteAdapter implements Adapter {

    private final RequestMapping requestMapping;

    public CoyoteAdapter(final RequestMapping requestMapping) {
        this.requestMapping = requestMapping;
    }

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) throws Exception {
        requestMapping.getController(httpRequest).service(httpRequest, httpResponse);
    }
}
