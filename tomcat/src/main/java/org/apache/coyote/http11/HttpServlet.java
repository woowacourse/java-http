package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class HttpServlet {

    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";

    protected void service(final HttpRequest req, final HttpResponse res) throws IOException, URISyntaxException {
        if (req.isSameMethod(METHOD_GET)) {
            doGet(req, res);
        }
        if (req.isSameMethod(METHOD_POST)) {
            doPost(req, res);
        }
    }

    public void doGet(final HttpRequest req, final HttpResponse resp) throws IOException {
        throw new UnsupportedOperationException();
    }

    public void doPost(final HttpRequest req, final HttpResponse resp) {
        throw new UnsupportedOperationException();
    }
}
