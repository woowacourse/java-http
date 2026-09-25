package org.apache.coyote.http11;

import java.io.IOException;
import org.apache.catalina.session.Session;

public class StaticResourceController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request, Session session) throws IOException {
        if ("/".equals(request.getPath())) {
            return resourceResponse(request, session, "static/index.html");
        }

        return resourceResponse(request, session, "static" + request.getPath());
    }

    @Override
    protected HttpResponse doPost(HttpRequest request, Session session) throws IOException {
        return doGet(request, session);
    }
}
