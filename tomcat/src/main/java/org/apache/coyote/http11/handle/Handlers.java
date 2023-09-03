package org.apache.coyote.http11.handle;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.start.HttpExtension;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.apache.coyote.http11.request.start.HttpExtension.*;

public class Handlers {
    private static final Map<HttpExtension, Handler> handlers;


    static {
        handlers = new HashMap<>();
        handlers.put(HTML, new HtmlHandler());
        handlers.put(CSS, new CssHandler());
        handlers.put(QUERY, new QueryHandler());
        handlers.put(JS, new JSHandler());
        handlers.put(ICO, new IcoHandler());
        handlers.put(DEFAULT, new DefaulHandler());
    }

    public static HttpResponse from(final HttpRequest request) throws IOException {
        final Handler handler = handlers.get(request.getHttpStartLine().getExtension());
        return handler.handle(request);
    }
}
