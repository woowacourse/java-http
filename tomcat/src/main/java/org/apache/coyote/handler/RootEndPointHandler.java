package org.apache.coyote.handler;

import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponseGenerator;

public class RootEndPointHandler extends Handler{
    private static final RootEndPointHandler INSTANCE = new RootEndPointHandler();

    private RootEndPointHandler() {
    }

    public static RootEndPointHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public String handle(HttpRequest httpRequest) {
        final String responseBody = "Hello world!";
        return HttpResponseGenerator.getOkResponse("text/html", responseBody);
    }
}
