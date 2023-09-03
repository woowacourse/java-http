package kokodak.handler;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import kokodak.http.HttpRequest;
import kokodak.http.HttpResponse;
import kokodak.http.RequestTarget;

public class MappingHandler {

    private static Map<String, Handler> handlers;

    static {
        handlers = new HashMap<>();
        handlers.put("resource", new ResourceHandler());
        handlers.put("/", new BasicHandler());
        handlers.put("/login", new LoginHandler());
    }

    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        final RequestTarget requestTarget = httpRequest.getRequestTarget();
        final String path = requestTarget.getPath();
        if (hasResource(path)) {
            final String fileName = "static" + path;
            final URL resourceUrl = getClass().getClassLoader().getResource(fileName);
            if (resourceUrl == null) {
                return new NotFoundHandler().handle(httpRequest);
            }
            final Handler handler = handlers.get("resource");
            return handler.handle(httpRequest);
        }
        final Handler handler = handlers.getOrDefault(path, new NotFoundHandler());
        return handler.handle(httpRequest);
    }

    private boolean hasResource(final String path) {
        final String lastPathSnippet = getLastPathSnippet(path);
        if (lastPathSnippet.contains(".")) {
            return true;
        }
        return false;
    }

    private String getLastPathSnippet(final String path) {
        final String[] pathSnippets = path.split("/");
        return pathSnippets[pathSnippets.length - 1];
    }
}
