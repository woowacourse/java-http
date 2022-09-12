package org.apache.catalina.handler;

import org.apache.coyote.http11.Resource;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ResourceHandler {

    public static HttpResponse render(final HttpRequest request) {
        return new HttpResponse.Builder(request)
            .ok()
            .messageBody(getResourceFrom(request))
            .build();
    }

    private static Resource getResourceFrom(final HttpRequest request) {
        return Resource.from(
            addExtension(request.getPath()));
    }

    private static String addExtension(final String path) {
        if (path.contains(".")) {
            return path;
        }
        return path + ".html";
    }

    private ResourceHandler() {
    }
}
