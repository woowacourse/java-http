package kokodak.handler;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kokodak.http.HttpRequest;
import kokodak.http.HttpResponse;
import kokodak.http.RequestTarget;
import kokodak.http.Session;

public class MappingHandler {

    private static Map<Class<? extends Argument>, ArgumentResolver> argumentResolvers;
    private static Map<String, Handler> handlers;

    static {
        argumentResolvers = new HashMap<>();
        argumentResolvers.put(Session.class, new SessionArgumentResolver());

        handlers = new HashMap<>();
        handlers.put("/", new BasicHandler());
        handlers.put("/login", new LoginHandler());
        handlers.put("/register", new RegisterHandler());
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
            final Handler handler = new ResourceHandler();
            return handler.handle(httpRequest);
        }
        final Handler handler = handlers.getOrDefault(path, new NotFoundHandler());
        final List<Class<? extends Argument>> requiredArguments = handler.requiredArguments();
        if (requiredArguments.isEmpty()) {
            return handler.handle(httpRequest);
        }
        final List<Argument> arguments = new ArrayList<>();
        requiredArguments.forEach(aClass -> {
            final ArgumentResolver argumentResolver = argumentResolvers.get(aClass);
            final Argument argument = (Argument) argumentResolver.resolve(httpRequest);
            arguments.add(argument);
        });
        handler.setArguments(arguments);
        return handler.handle(httpRequest);
    }

    private boolean hasResource(final String path) {
        final String lastPathSnippet = getLastPathSnippet(path);
        return lastPathSnippet.contains(".");
    }

    private String getLastPathSnippet(final String path) {
        if ("/".equals(path)) {
            return "";
        }
        final String[] pathSnippets = path.split("/");
        return pathSnippets[pathSnippets.length - 1];
    }
}
