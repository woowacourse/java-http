package org.apache.coyote.http11.handle;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Handlers {
    private static final Map<String, Handler> handlers;


    static {
        handlers = new HashMap<>();
        handlers.put(".html", new HtmlHandler());
        handlers.put(".css", new CssHandler());
        handlers.put("?", new QueryHandler());
        handlers.put(".js", new JSHandler());
        handlers.put(".ico", new IcoHandler());
    }

    public static HttpResponse from(final HttpRequest request) throws IOException {
        final Handler handler = handlers.get(request.getHttpStartLine().getRequestTarget().getExtension());
        return handler.handle(request);
    }
}
