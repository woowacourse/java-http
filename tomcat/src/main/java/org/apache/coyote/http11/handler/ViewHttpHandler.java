package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.net.URL;
import org.apache.coyote.http11.ResourceToHttpBodyConverter;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public class ViewHttpHandler implements HttpHandler {

    private final String viewName;

    public ViewHttpHandler(String viewName) {
        this.viewName = viewName;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        String fileName = "static/" + viewName + ".html";
        URL resource = getClass().getClassLoader()
                .getResource(fileName);

        HttpResponse response = ResourceToHttpBodyConverter.covert(resource);
        return response;
    }
}
