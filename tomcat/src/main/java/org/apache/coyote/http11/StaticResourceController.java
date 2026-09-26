package org.apache.coyote.http11;

import java.io.IOException;
import java.util.List;
import org.apache.catalina.session.Session;

public class StaticResourceController extends AbstractController {

    @Override
    protected List<String> getAllowedMethods() {
        return List.of("GET");
    }

    @Override
    protected HttpResponse doGet(HttpRequest request, Session session) throws IOException {
        if ("/".equals(request.getPath())) {
            return resourceResponse(request, session, "static/index.html");
        }

        return resourceResponse(request, session, "static" + request.getPath());
    }
}
