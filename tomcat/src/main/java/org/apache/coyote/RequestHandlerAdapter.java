package org.apache.coyote;

import org.apache.coyote.handler.StaticResourceHandler;
import org.apache.coyote.mapping.UrlHandlerMapping;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponseGenerator;

public class RequestHandlerAdapter {
    private static final String PATH_DELIMITER = "/";

    // TODO: mapping handler method
    public String handle(final HttpRequest httpRequest) {
        final String path = httpRequest.getPath();
        final String[] paths = path.split(PATH_DELIMITER);

        if (paths.length == 0) {
            return handleRoot();
        }

        final String resourceName = paths[paths.length - 1];
        if (resourceName.contains(".")) {
            return StaticResourceHandler.getInstance().handle(new HttpRequest("GET", "404.html", "HTTP/1.1", null, null));
        }

        return UrlHandlerMapping.getInstance().mapping(httpRequest);
    }

    private String handleRoot() {
        final String responseBody = "Hello world!";
        return HttpResponseGenerator.getOkResponse("text/html", responseBody);
    }
}
