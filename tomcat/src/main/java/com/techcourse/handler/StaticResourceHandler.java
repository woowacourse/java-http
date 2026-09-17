package com.techcourse.handler;

import java.io.IOException;
import org.apache.catalina.handler.ResourceHandler;
import org.apache.catalina.resource.ResourcePathResolver;
import org.apache.catalina.resource.StaticResourceLoader;
import org.apache.coyote.http.HttpServletRequest;
import org.apache.coyote.http.HttpServletResponse;
import org.apache.coyote.http.MimeType;
import org.apache.coyote.http.StaticResourceBody;

public class StaticResourceHandler implements ResourceHandler {

    private final StaticResourceLoader loader = new StaticResourceLoader();

    @Override
    public boolean canHandle(HttpServletRequest request) {
        return true;
    }

    @Override
    public HttpServletResponse handle(HttpServletRequest request) throws IOException {
        final String path = ResourcePathResolver.resolve(request.path());
        return HttpServletResponse.ok(new StaticResourceBody(loader.load(path), MimeType.fromPath(path)));
    }
}
