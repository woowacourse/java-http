package org.apache.coyote.http11.resolver;

import java.util.List;
import org.apache.coyote.http11.data.Request;
import org.apache.coyote.http11.data.Response;
import org.apache.coyote.http11.hadler.RequestHandler;
import org.apache.coyote.http11.hadler.RootRequestHandler;
import org.apache.coyote.http11.hadler.StaticResourceRequestHandler;

public class ServletResolver implements RequestResolver {
    private final List<RequestHandler> servlets = List.of(
            new StaticResourceRequestHandler(),
            new RootRequestHandler()
    );

    @Override
    public Response handleRequest(Request request) {
        for (RequestHandler servlet : servlets) {
            if (servlet.canHandle(request)) {
                return servlet.handle(request);
            }
        }

        return Response.notFound();
    }

    @Override
    public boolean canHandle(Request request) {
      return true;
    }
}