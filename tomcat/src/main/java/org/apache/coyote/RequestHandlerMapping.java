package org.apache.coyote;

import java.util.Map;
import java.util.function.Predicate;
import org.apache.ResourceReader;
import org.apache.coyote.handler.LoginRequestHandler;
import org.apache.coyote.handler.NotFoundHandler;
import org.apache.coyote.handler.RootRequestHandler;
import org.apache.coyote.handler.SignupRequestHandler;
import org.apache.coyote.handler.StaticResourceRequestHandler;

public class RequestHandlerMapping {

    private static final Map<Predicate<HttpRequest>, RequestHandler> mapper = Map.ofEntries(
            Map.entry((request) -> "/login".equals(request.getPath()), new LoginRequestHandler()),
            Map.entry((request) -> "/register".equals(request.getPath()), new SignupRequestHandler()),
            Map.entry((request) -> "/".equals(request.getPath()), new RootRequestHandler()),
            Map.entry((request) -> ResourceReader.canRead(request.getRequestURI()), new StaticResourceRequestHandler())
    );

    public RequestHandler getRequestHandler(HttpRequest httpRequest) {
        return mapper.entrySet()
                .stream()
                .filter(entry -> entry.getKey().test(httpRequest))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(new NotFoundHandler());
    }
}
