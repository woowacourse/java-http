package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.net.URL;
import org.apache.coyote.http11.ResourceToHttpBodyConverter;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public class StaticResourceHttpHandler implements HttpHandler {

    private static final String LINE_FEED = "\n";

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        String fileName = "static" + request.getUrlPath();
        URL resource = getClass().getClassLoader()
                .getResource(fileName);

        return ResourceToHttpBodyConverter.covert(resource);
    }
}
