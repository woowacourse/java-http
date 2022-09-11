package org.apache.coyote.http11;

import java.io.BufferedReader;
import org.apache.coyote.http11.controller.ControllerMapper;
import org.apache.coyote.http11.controller.Handler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class Servlet {

    private HttpRequest request;
    private HttpResponse response;

    public Servlet(final BufferedReader reader) {
        this.request = HttpRequest.from(new BufferedReader(reader));
        this.response = new HttpResponse();
    }

    public void service() {
        final Handler controller = ControllerMapper.findController(request.getUrl());
        controller.handle(request, response);
    }

    public HttpRequest getRequest() {
        return request;
    }

    public HttpResponse getResponse() {
        return response;
    }
}
