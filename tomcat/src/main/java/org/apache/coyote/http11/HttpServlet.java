package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class HttpServlet {

    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";

    protected void service(HttpRequest req, HttpResponse res) throws IOException, URISyntaxException {
        if (req.isSameMethod(METHOD_GET)) {
            doGet(req, res);
        }
        if (req.isSameMethod(METHOD_POST)) {
            doPost(req, res);
        }
    }

    public void doGet(HttpRequest req, HttpResponse resp) throws IOException, URISyntaxException {
        throw new IllegalArgumentException("method get not supported");
    }

    public void doPost(HttpRequest req, HttpResponse resp) {
        throw new IllegalArgumentException("method post not supported");
    }
}
